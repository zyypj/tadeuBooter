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
            val tabProv = mutableMapOf<List<String>, Method>()

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
                    rootExec?.getAnnotation(AllowedSenders::class.java)?.let { asn ->
                        if (sender is Player && asn.value == SenderType.CONSOLE ||
                            sender !is Player && asn.value == SenderType.PLAYER
                        ) {
                            sender.sendMessage(asn.errorMessage)
                            return true
                        }
                    }

                    if (cmdAnn.permission.isNotEmpty() && !sender.hasPermission(cmdAnn.permission)) {
                        sender.sendMessage(cmdAnn.permissionMessage)
                        return true
                    }

                    val argsList = args.toList()
                    val match = subExec.keys.sortedByDescending { it.size }
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

                    if (sender is Player) {
                        method?.getAnnotation(Cooldown::class.java)?.let { cd ->
                            val ctrl = CooldownController.getCooldownController(sender)
                            val key =
                                cd.key.ifEmpty { "${plugin.name}.${cmdAnn.name}.${match?.joinToString(".") ?: "root"}" }
                            if (ctrl.isInCooldown(key)) {
                                val rem = (ctrl.getCooldown(key) / 1000).coerceAtLeast(1)
                                sender.sendMessage(cd.message.replace("{time}", rem.toString()))
                                return true
                            }
                            ctrl.createCooldown(key, cd.seconds)
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
                            val allowedRoot = cmdAnn.permission.isEmpty() || sender.hasPermission(cmdAnn.permission)
                            val allowedSub = subAnn.filterKeys { it[0] == seg }.values.any {
                                it.permission.isEmpty() || sender.hasPermission(it.permission)
                            }
                            allowedRoot && (allowedSub || subAnn.keys.none { it[0] == seg })
                        }
                    } else {
                        val match = subExec.keys.sortedByDescending { it.size }
                            .firstOrNull { path -> argsList.size - 1 >= path.size && argsList.take(path.size) == path }
                        match?.let { path ->
                            tabProv[path]?.invoke(
                                handler,
                                sender,
                                argsList.drop(path.size).toTypedArray()
                            ) as? List<String>
                        } ?: emptyList()
                    }
                }
            }
            commandMap.register(plugin.name.lowercase(), bukkitCmd)
        }
    }

    private fun fetchCommandMap(): CommandMap {
        val pm = Bukkit.getPluginManager()
        val field: Field = pm.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(pm) as CommandMap
    }
}