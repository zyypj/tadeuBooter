package com.github.zyypj.tadeuBooter.minecraft.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private String noPermissionMessage = "§4Você não tem permissão para executar este comando.";
    private String invalidUsageMessage = "§4Uso incorreto. Tente: ";
    private String playerOnlyMessage = "§4Este comando só pode ser usado por jogadores.";

    public CommandManager(String command) {
        Bukkit.getPluginCommand(command).setExecutor(this);
        Bukkit.getPluginCommand(command).setTabCompleter(this);
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public void setNoPermissionMessage(String message) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes('&', message);
    }

    public void setInvalidUsageMessage(String message) {
        this.invalidUsageMessage = ChatColor.translateAlternateColorCodes('&', message);
    }

    public void setPlayerOnlyMessage(String message) {
        this.playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(invalidUsageMessage + "/" + label + " <subcomando>");
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(invalidUsageMessage + "/" + label + " <subcomando>");
            return true;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(playerOnlyMessage);
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(noPermissionMessage);
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String subCommand : subCommands.keySet()) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                completions.addAll(subCommand.tabComplete(sender, args));
            }
        }
        return completions;
    }
}