package me.zyypj.booter.minecraft.spigot.command.core

import me.zyypj.booter.minecraft.spigot.command.annotation.*
import me.zyypj.booter.minecraft.spigot.cooldown.CooldownController
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

/**
 * Registra comandos anotados dinamicamente e gerencia execução, permissões, cooldown e métricas.
 */
object CommandRegister {

    private data class Stat(var count: Long = 0, var totalNanos: Long = 0)

    private val stats = ConcurrentHashMap<String, Stat>()

    /**
     * Registra tempo de execução de comandos para métricas.
     */
    fun record(key: String, nanos: Long) {
        stats.merge(key, Stat(1, nanos)) { old, new ->
            old.count += new.count
            old.totalNanos += new.totalNanos
            old
        }
    }

    /**
     * Retorna mapa de métricas: <chave, (count, ms total)>.
     */
    fun report(): Map<String, Pair<Long, Long>> =
        stats.mapValues { (_, st) -> Pair(st.count, st.totalNanos / 1_000_000) }

    /**
     * Registra instâncias `handler` como comandos anotados.
     */
    @JvmStatic
    fun registerCommands(plugin: JavaPlugin, vararg handlers: Any) {
        val commandMap = try {
            fetchCommandMap(plugin)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Não foi possível acessar CommandMap", e)
            return
        }

        handlers.forEach { handler ->
            val clazz = handler.javaClass
            val cmdAnn = clazz.getAnnotation(Command::class.java) ?: return@forEach
            val autoHelp = clazz.getAnnotation(AutoHelp::class.java)

            // coleta métodos anotados
            val subExec = mutableMapOf<List<String>, Method>()
            val subAnn = mutableMapOf<List<String>, SubCommand>()
            var rootExec: Method? = null
            var rootTab: Method? = null
            val tabProv = mutableMapOf<List<String>, Method>()

            clazz.declaredMethods.forEach { m ->
                m.isAccessible = true
                if (m.isAnnotationPresent(Execute::class.java)) {
                    m.getAnnotation(SubCommand::class.java)?.let { sc ->
                        subExec[sc.path.toList()] = m
                        subAnn[sc.path.toList()] = sc
                    } ?: run { rootExec = m }
                }
                m.getAnnotation(TabComplete::class.java)?.let { tc ->
                    val path = tc.path.toList()
                    if (path.isEmpty()) rootTab = m
                    else tabProv[path] = m
                }
            }

            // cria comando Bukkit
            val bukkitCmd = object : org.bukkit.command.Command(
                cmdAnn.name,
                cmdAnn.description,
                cmdAnn.usage.ifEmpty { "/${cmdAnn.name}" },
                cmdAnn.aliases.toList()
            ) {
                override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                    return try {
                        handleExecute(
                            plugin, handler, cmdAnn, autoHelp, rootExec,
                            sender, args, subExec, subAnn
                        )
                    } catch (e: Exception) {
                        plugin.logger.log(Level.SEVERE, "Erro ao executar comando '${cmdAnn.name}'", e)
                        sender.sendMessage("§cOcorreu um erro interno ao executar o comando.")
                        true
                    }
                }

                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                    return try {
                        handleTabComplete(
                            handler, cmdAnn, sender, args,
                            subExec, subAnn, rootTab, tabProv
                        )
                    } catch (e: Exception) {
                        plugin.logger.log(Level.SEVERE, "Erro em tabComplete do comando '${cmdAnn.name}'", e)
                        emptyList()
                    }
                }
            }

            commandMap.register(plugin.name.lowercase(), bukkitCmd)
        }
    }

    private fun handleExecute(
        plugin: JavaPlugin,
        handler: Any,
        cmdAnn: Command,
        autoHelp: AutoHelp?,
        rootExec: Method?,
        sender: CommandSender,
        args: Array<String>,
        subExec: Map<List<String>, Method>,
        subAnn: Map<List<String>, SubCommand>
    ): Boolean {
        val argsList = args.toList()

        // ajuda automática
        if (autoHelp != null && (argsList.isEmpty() || argsList[0].equals("help", true))) {
            sender.sendMessage(autoHelp.header)
            autoHelp.title.takeIf { it.isNotEmpty() }?.let { sender.sendMessage(it) }
            sender.sendMessage("/${cmdAnn.name} ${cmdAnn.usage} - ${cmdAnn.description}")
            subAnn.forEach { (path, sc) ->
                sender.sendMessage("/${cmdAnn.name} ${path.joinToString(" ")} - ${sc.permission}")
            }
            return true
        }

        // allowed senders
        rootExec?.getAnnotation(AllowedSenders::class.java)?.let { asn ->
            if ((sender is Player && asn.value == SenderType.CONSOLE)
                || (sender !is Player && asn.value == SenderType.PLAYER)
            ) {
                sender.sendMessage(asn.errorMessage)
                return true
            }
        }

        // permissão do comando raiz
        if (cmdAnn.permission.isNotEmpty() && !sender.hasPermission(cmdAnn.permission)) {
            sender.sendMessage(cmdAnn.permissionMessage)
            return true
        }

        // subcomando
        val match = subExec.keys
            .sortedByDescending { it.size }
            .firstOrNull { path -> argsList.size >= path.size && argsList.take(path.size) == path }
        match?.let { path ->
            subAnn[path]?.let { sc ->
                if (sc.permission.isNotEmpty() && !sender.hasPermission(sc.permission)) {
                    sender.sendMessage(sc.permissionMessage)
                    return true
                }
            }
        }

        val method = match?.let { subExec[it] } ?: rootExec
        val methodArgs = match?.size?.let { argsList.drop(it).toTypedArray() } ?: args

        // validação de args
        method?.getAnnotationsByType(Validate::class.java)?.forEach { v ->
            if (methodArgs.size <= v.index || !validateArg(methodArgs[v.index], v.type)) {
                sender.sendMessage(v.errorMessage)
                return true
            }
        }

        // bind de parâmetros
        val params = bindParameters(method, sender, methodArgs)

        // cooldown
        if (sender is Player) {
            method?.getAnnotation(Cooldown::class.java)?.let { cd ->
                val ctrl = CooldownController.getCooldownController(sender)
                val key = cd.key.ifEmpty { "${plugin.name}.${cmdAnn.name}.${match?.joinToString(".") ?: "root"}" }
                if (ctrl.isInCooldown(key)) {
                    val rem = (ctrl.getCooldown(key) / 1000).coerceAtLeast(1)
                    sender.sendMessage(cd.message.replace("{time}", rem.toString()))
                    return true
                }
                ctrl.createCooldown(key, cd.seconds)
            }
        }

        // métrica
        val metricAnn = method?.getAnnotation(Metric::class.java)
        val metricKey = metricAnn?.name.takeIf { !it.isNullOrEmpty() }
            ?: "${handler.javaClass.simpleName}.${method?.name}"
        val start = System.nanoTime()

        // invocação segura
        val result = try {
            method?.invoke(handler, *params.toTypedArray()) as? Boolean ?: false
        } catch (e: InvocationTargetException) {
            plugin.logger.log(Level.SEVERE, "Erro no método ${method?.name}", e.targetException)
            sender.sendMessage("§cOcorreu um erro interno ao executar o comando.")
            false
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Falha ao invocar método ${method?.name}", e)
            false
        }

        record(metricKey, System.nanoTime() - start)
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleTabComplete(
        handler: Any,
        cmdAnn: Command,
        sender: CommandSender,
        args: Array<String>,
        subExec: Map<List<String>, Method>,
        subAnn: Map<List<String>, SubCommand>,
        rootTab: Method?,
        tabProv: Map<List<String>, Method>
    ): List<String> {
        val argsList = args.toList()
        if (argsList.size == 1) {
            val suggestions = rootTab?.invoke(handler, sender, args) as? List<String>
                ?: subExec.keys.map { it[0] }.distinct()
            return suggestions.filter { sugg ->
                subExec.keys.any { path ->
                    path[0] == sugg && (subAnn[path]?.permission.isNullOrEmpty() || sender.hasPermission(subAnn[path]!!.permission))
                }
            }
        }

        val match = subExec.keys
            .sortedByDescending { it.size }
            .firstOrNull { path -> argsList.size - 1 >= path.size && argsList.take(path.size) == path }
            ?: return emptyList()
        val provider = tabProv[match] ?: return emptyList()
        val subArgs = argsList.drop(match.size).toTypedArray()
        return (provider.invoke(handler, sender, subArgs) as? List<String>) ?: emptyList()
    }

    private fun validateArg(arg: String, type: ArgType): Boolean = when (type) {
        ArgType.INT -> arg.toIntOrNull() != null
        ArgType.LONG -> arg.toLongOrNull() != null
        ArgType.DOUBLE -> arg.toDoubleOrNull() != null
        ArgType.BOOLEAN -> arg.equals("true", true) || arg.equals("false", true)
        ArgType.STRING -> true
    }

    private fun bindParameters(
        method: Method?,
        sender: CommandSender,
        args: Array<String>
    ): List<Any?> {
        val params = mutableListOf<Any?>()
        method?.parameters?.forEachIndexed { idx, p ->
            when {
                CommandSender::class.java.isAssignableFrom(p.type) -> params.add(sender)
                p.isAnnotationPresent(Param::class.java) -> {
                    val pa = p.getAnnotation(Param::class.java)
                    val raw = args.getOrNull(pa.index) ?: pa.default
                    val converted = when (pa.type) {
                        ArgType.INT -> raw.toIntOrNull()
                        ArgType.LONG -> raw.toLongOrNull()
                        ArgType.DOUBLE -> raw.toDoubleOrNull()
                        ArgType.BOOLEAN -> runCatching { raw.toBooleanStrict() }.getOrNull()
                        ArgType.STRING -> raw
                    }
                    params.add(converted)
                }

                p.type.isArray && p.type.componentType == String::class.java -> params.add(args)
                else -> params.add(null)
            }
        }
        return params
    }

    /**
     * Obtém o CommandMap do servidor via reflexão, lançando se falhar.
     */
    private fun fetchCommandMap(plugin: JavaPlugin): CommandMap {
        try {
            val pm = Bukkit.getPluginManager()
            val field: Field = pm.javaClass.getDeclaredField("commandMap")
            field.isAccessible = true
            return field.get(pm) as CommandMap
        } catch (e: Exception) {
            throw IllegalStateException("Falha ao obter CommandMap via reflexão", e)
        }
    }
}