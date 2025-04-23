package com.github.zyypj.tadeuBooter.api.minecraft.command.core

import Cooldown
import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.*
import com.github.zyypj.tadeuBooter.api.minecraft.cooldown.CooldownController
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object MetricManager {
    private data class Stat(var count: Long = 0, var totalNanos: Long = 0)

    private val stats = ConcurrentHashMap<String, Stat>()
    fun record(key: String, nanos: Long) {
        stats.compute(key) { _, st ->
            val s = st ?: Stat()
            s.count++
            s.totalNanos += nanos
            s
        }
    }

    fun report(): Map<String, Pair<Long, Long>> =
        stats.mapValues { (_, st) -> Pair(st.count, st.totalNanos / 1_000_000) }
}

object CommandRegister {
    @JvmStatic
    fun registerCommands(plugin: JavaPlugin, vararg handlers: Any) {
        val commandMap = fetchCommandMap()

        handlers.forEach { handler ->
            val clazz = handler.javaClass
            val cmdAnn = clazz.getAnnotation(Command::class.java) ?: return@forEach
            val autoHelp = clazz.getAnnotation(AutoHelp::class.java)

            val subExec = mutableMapOf<List<String>, Method>()
            val subAnn = mutableMapOf<List<String>, SubCommand>()
            var rootExec: Method? = null
            var rootTab: Method? = null
            val tabProv = mutableMapOf<List<String>, Method>()

            clazz.declaredMethods.forEach { m ->
                m.isAccessible = true
                if (m.isAnnotationPresent(Execute::class.java)) {
                    m.getAnnotation(SubCommand::class.java)?.let { sc ->
                        val path = sc.path.toList()
                        subExec[path] = m
                        subAnn[path] = sc
                    } ?: run { rootExec = m }
                }
                m.getAnnotation(TabComplete::class.java)?.let { tc ->
                    val path = tc.path.toList()
                    if (path.isEmpty()) rootTab = m
                    else tabProv[path] = m
                }
            }

            val bukkitCmd = object : org.bukkit.command.Command(
                cmdAnn.name,
                cmdAnn.description,
                cmdAnn.usage.ifEmpty { "/${cmdAnn.name}" },
                cmdAnn.aliases.toList()
            ) {
                override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                    val argsList = args.toList()

                    if (autoHelp != null && (argsList.isEmpty() || argsList[0].equals("help", true))) {
                        sender.sendMessage(autoHelp.header)
                        autoHelp.title.takeIf { it.isNotEmpty() }?.let { sender.sendMessage(it) }
                        rootExec?.let { sender.sendMessage("/${cmdAnn.name} ${cmdAnn.usage} - ${cmdAnn.description}") }
                        subAnn.forEach { (path, sc) ->
                            val usage = path.joinToString(" ")
                            sender.sendMessage("/${cmdAnn.name} $usage - ${sc.permission}")
                        }
                        return true
                    }

                    rootExec?.getAnnotation(AllowedSenders::class.java)?.let { asn ->
                        if ((sender is Player && asn.value == SenderType.CONSOLE)
                            || (sender !is Player && asn.value == SenderType.PLAYER)
                        ) {
                            sender.sendMessage(asn.errorMessage)
                            return true
                        }
                    }

                    if (cmdAnn.permission.isNotEmpty() && !sender.hasPermission(cmdAnn.permission)) {
                        sender.sendMessage(cmdAnn.permissionMessage)
                        return true
                    }

                    val matchPath = subExec.keys.sortedByDescending { it.size }
                        .firstOrNull { path -> argsList.size >= path.size && argsList.take(path.size) == path }
                    matchPath?.let { sc ->
                        subAnn[sc]?.let { scAnn ->
                            if (scAnn.permission.isNotEmpty() && !sender.hasPermission(scAnn.permission)) {
                                sender.sendMessage(scAnn.permissionMessage)
                                return true
                            }
                        }
                    }

                    val method = matchPath?.let { subExec[it] } ?: rootExec
                    val methodArgs = matchPath?.size?.let { argsList.drop(it).toTypedArray() } ?: args

                    method?.getAnnotationsByType(Validate::class.java)?.forEach { v ->
                        if (methodArgs.size <= v.index || !validateArg(methodArgs[v.index], v.type)) {
                            sender.sendMessage(v.errorMessage)
                            return true
                        }
                    }

                    val params = bindParameters(method, sender, methodArgs)

                    if (sender is Player) {
                        method?.getAnnotation(Cooldown::class.java)?.let { cd ->
                            val ctrl = CooldownController.getCooldownController(sender)
                            val key =
                                cd.key.ifEmpty { "${plugin.name}.${cmdAnn.name}.${matchPath?.joinToString(".") ?: "root"}" }
                            if (ctrl.isInCooldown(key)) {
                                val rem = (ctrl.getCooldown(key) / 1000).coerceAtLeast(1)
                                sender.sendMessage(cd.message.replace("{time}", rem.toString()))
                                return true
                            }
                            ctrl.createCooldown(key, cd.seconds)
                        }
                    }

                    val metricAnn = method?.getAnnotation(Metric::class.java)
                    val metricKey = metricAnn?.name.takeIf { !it.isNullOrEmpty() }
                        ?: "${clazz.simpleName}.${method?.name}"
                    val start = System.nanoTime()

                    val result = method?.invoke(handler, *params.toTypedArray()) as? Boolean ?: false
                    val elapsed = System.nanoTime() - start
                    metricAnn?.let { MetricManager.record(metricKey, elapsed) }

                    return result
                }

                @Suppress("UNCHECKED_CAST")
                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                    val argsList = args.toList()

                    if (argsList.size == 1) {
                        val rootSuggestions = rootTab?.invoke(handler, sender, args) as? List<String>
                            ?: subExec.keys.map { it[0] }.distinct()
                        return rootSuggestions.filter { sugg ->
                            val paths = subExec.keys.filter { it[0] == sugg }
                            paths.any { path ->
                                subAnn[path]?.let { sc ->
                                    sc.permission.isEmpty() || sender.hasPermission(sc.permission)
                                } ?: true
                            }
                        }
                    }

                    val matchPath = subExec.keys.sortedByDescending { it.size }
                        .firstOrNull { path -> argsList.size - 1 >= path.size && argsList.take(path.size) == path }
                    val provider = matchPath?.let { tabProv[it] } ?: return emptyList()
                    val subArgs = argsList.drop(matchPath.size).toTypedArray()
                    return provider.invoke(handler, sender, subArgs) as? List<String> ?: emptyList()
                }
            }

            commandMap.register(plugin.name.lowercase(), bukkitCmd)
        }
    }

    private fun validateArg(arg: String, type: ArgType): Boolean = when (type) {
        ArgType.INT -> arg.toIntOrNull() != null
        ArgType.LONG -> arg.toLongOrNull() != null
        ArgType.DOUBLE -> arg.toDoubleOrNull() != null
        ArgType.BOOLEAN -> arg.equals("true", true) || arg.equals("false", true)
        ArgType.STRING -> true
    }

    private fun bindParameters(
        method: Method?, sender: CommandSender, args: Array<String>
    ): List<Any?> {
        val params = mutableListOf<Any?>()
        method?.parameters?.forEachIndexed { _, p ->
            when {
                CommandSender::class.java.isAssignableFrom(p.type) -> params.add(sender)
                p.isAnnotationPresent(Param::class.java) -> {
                    val pa = p.getAnnotation(Param::class.java)
                    val raw = args.getOrNull(pa.index) ?: pa.default
                    val converted = when (pa.type) {
                        ArgType.INT -> raw.toIntOrNull()
                        ArgType.LONG -> raw.toLongOrNull()
                        ArgType.DOUBLE -> raw.toDoubleOrNull()
                        ArgType.BOOLEAN -> raw.toBooleanStrictOrNull()
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

    private fun fetchCommandMap(): CommandMap {
        val pm = Bukkit.getPluginManager()
        val field: Field = pm.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(pm) as CommandMap
    }
}