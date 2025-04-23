package com.github.zyypj.tadeuBooter.api.minecraft.command.core

import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.Command
import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.Execute
import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.SubCommand
import com.github.zyypj.tadeuBooter.api.minecraft.command.annotation.TabComplete
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
    /**
     * Registra cada handler anotado passado explicitamente.
     * Exemplo no onEnable:
     * CommandRegister.registerCommands(this, WarpCommand(), TesteCommand())
     */
    @JvmStatic
    fun registerCommands(plugin: JavaPlugin, vararg handlers: Any) {
        val commandMap = fetchCommandMap()

        handlers.forEach { handler ->
            val clazz = handler.javaClass
            val cmdAnn = clazz.getAnnotation(Command::class.java) ?: return@forEach

            val subExec = mutableMapOf<String, Method>()
            val subAnnotations = mutableMapOf<String, SubCommand>()
            var rootExec: Method? = null
            var rootTab: Method? = null
            val subTab = mutableMapOf<String, Method>()

            clazz.declaredMethods.forEach { m ->
                m.isAccessible = true
                if (m.isAnnotationPresent(Execute::class.java)) {
                    val sc = m.getAnnotation(SubCommand::class.java)
                    if (sc != null) {
                        subExec[sc.name] = m
                        subAnnotations[sc.name] = sc
                    } else rootExec = m
                }
                if (m.isAnnotationPresent(TabComplete::class.java)) {
                    val tc = m.getAnnotation(TabComplete::class.java)
                    if (tc != null) {
                        if (tc.forSub.isBlank()) rootTab = m
                        else subTab[tc.forSub] = m
                    }
                }
            }

            val bukkitCmd = object : org.bukkit.command.Command(
                cmdAnn.name, cmdAnn.description, cmdAnn.usage.ifEmpty { "/${cmdAnn.name}" }, cmdAnn.aliases.toList()
            ) {
                override fun execute(
                    sender: CommandSender, label: String, args: Array<String>
                ): Boolean {
                    if (cmdAnn.permission.isNotEmpty() && !sender.hasPermission(cmdAnn.permission)) {
                        sender.sendMessage(cmdAnn.permissionMessage)
                        return true
                    }

                    val subName = args.getOrNull(0)
                    val method = if (subName != null && subExec.containsKey(subName)) subExec[subName] else rootExec
                    val methodArgs = if (subExec.containsKey(subName)) args.drop(1).toTypedArray() else args
                    if (subName != null && subAnnotations.containsKey(subName)) {
                        val scAnn = subAnnotations[subName]!!
                        if (scAnn.permission.isNotEmpty() && !sender.hasPermission(scAnn.permission)) {
                            sender.sendMessage(scAnn.permissionMessage)
                            return true
                        }
                    }

                    return invokeCommand(method, handler, sender, methodArgs)
                }

                override fun tabComplete(
                    sender: CommandSender, alias: String, args: Array<String>
                ): List<String> {
                    return if (args.size <= 1) {
                        val suggestions = rootTab?.let {
                            invokeTab(it, handler, sender, args)
                        } ?: subExec.keys.toList()

                        suggestions.filter { name ->
                            if (subAnnotations.containsKey(name)) {
                                val scAnn = subAnnotations[name]!!
                                scAnn.permission.isEmpty() || sender.hasPermission(scAnn.permission)
                            } else true
                        }
                    } else {
                        val subName = args[0]
                        val method = subTab[subName]
                        invokeTab(method, handler, sender, args.drop(1).toTypedArray())
                    }
                }
            }

            commandMap.register(plugin.name.lowercase(), bukkitCmd)
        }
    }

    private fun invokeCommand(
        method: Method?, instance: Any, sender: CommandSender, args: Array<String>
    ): Boolean = method?.invoke(instance, sender, args) as? Boolean ?: false

    @Suppress("UNCHECKED_CAST")
    private fun invokeTab(
        method: Method?, instance: Any, sender: CommandSender, args: Array<String>
    ): List<String> = method?.invoke(instance, sender, args) as? List<String> ?: emptyList()

    private fun fetchCommandMap(): CommandMap {
        val pm = Bukkit.getPluginManager()
        val field: Field = pm.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(pm) as CommandMap
    }
}
