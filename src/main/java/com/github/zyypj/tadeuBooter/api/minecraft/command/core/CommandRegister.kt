package com.github.zyypj.tadeuBooter.api.minecraft.command.core

import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Registra comandos manualmente fornecidos como instâncias, usando anotações.
 */
object CommandRegister {

    @JvmStatic
    fun registerCommands(plugin: JavaPlugin, vararg handlers: Any) {
        val commandMap = fetchCommandMap()

        handlers.forEach { handler ->
            val clazz = handler.javaClass
            val cmdAnn = clazz.getAnnotation(Command::class.java) ?: return@forEach

            val subExec = mutableMapOf<List<String>, Method>()
            val subAnn = mutableMapOf<List<String>, SubCommand>()
            var rootExec: Method? = null
            var rootTab: Method? = null
            val tabProviders = mutableMapOf<List<String>, Method>()

            clazz.declaredMethods.forEach { m ->
                m.isAccessible = true
                if (m.isAnnotationPresent(Execute::class.java)) {
                    val sc = m.getAnnotation(SubCommand::class.java)
                    if (sc != null) {
                        val path = sc.path.toList()
                        subExec[path] = m
                        subAnn[path] = sc
                    } else rootExec = m
                }
                if (m.isAnnotationPresent(TabComplete::class.java)) {
                    val tc = m.getAnnotation(TabComplete::class.java)
                    val path = tc.path.toList()
                    if (path.isEmpty()) rootTab = m
                    else tabProviders[path] = m
                }
            }

            val bcmd = object : org.bukkit.command.Command(
                cmdAnn.name, cmdAnn.description,
                cmdAnn.usage.ifEmpty { "/${cmdAnn.name}" },
                cmdAnn.aliases.toList()
            ) {
                override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                    // valida permissão raiz
                    if (cmdAnn.permission.isNotEmpty() && !sender.hasPermission(cmdAnn.permission)) {
                        sender.sendMessage(cmdAnn.permissionMessage)
                        return true
                    }

                    val argsList = args.toList()
                    val match = subExec.keys
                        .sortedByDescending { it.size }
                        .firstOrNull { path -> argsList.size >= path.size && argsList.take(path.size) == path }

                    // checa permissão do sub
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

                    // validações via @Validate
                    method?.getAnnotationsByType(Validate::class.java)?.forEach { v ->
                        if (methodArgs.size <= v.index) {
                            sender.sendMessage(v.errorMessage)
                            return true
                        }
                        val a = methodArgs[v.index]
                        val ok = when (v.type) {
                            ArgType.INT -> a.toIntOrNull() != null
                            ArgType.LONG -> a.toLongOrNull() != null
                            ArgType.DOUBLE -> a.toDoubleOrNull() != null
                            ArgType.BOOLEAN -> a.equals("true", true) || a.equals("false", true)
                            ArgType.STRING -> true
                        }
                        if (!ok) {
                            sender.sendMessage(v.errorMessage)
                            return true
                        }
                    }

                    return method?.invoke(handler, sender, methodArgs) as? Boolean ?: false
                }

                @Suppress("UNCHECKED_CAST")
                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                    val argsList = args.toList()
                    return if (argsList.size <= 1) {
                        val base = rootTab?.invoke(handler, sender, args) as? List<String>
                            ?: subExec.keys.map { it[0] }.distinct()
                        base.filter { seg ->
                            subAnn.filterKeys { it[0] == seg }.any { (_, sc) ->
                                sc.permission.isEmpty() || sender.hasPermission(sc.permission)
                            } || cmdAnn.permission.isEmpty() || sender.hasPermission(cmdAnn.permission)
                        }
                    } else {
                        val match = subExec.keys
                            .sortedByDescending { it.size }
                            .firstOrNull { path -> argsList.size - 1 >= path.size && argsList.take(path.size) == path }
                        match?.let { path ->
                            tabProviders[path]?.invoke(
                                handler,
                                sender,
                                argsList.drop(path.size).toTypedArray()
                            ) as? List<String>
                        } ?: emptyList()
                    }
                }
            }

            commandMap.register(plugin.name.lowercase(), bcmd)
        }
    }

    private fun fetchCommandMap(): CommandMap {
        val pm = Bukkit.getPluginManager()
        val field: Field = pm.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(pm) as CommandMap
    }
}