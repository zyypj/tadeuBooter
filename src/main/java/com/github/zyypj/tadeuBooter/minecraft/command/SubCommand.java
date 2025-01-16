package com.github.zyypj.tadeuBooter.minecraft.command;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {

    public abstract String getName(); // Nome do subcomando
    public abstract String getPermission(); // Permissão necessária
    public abstract boolean isPlayerOnly(); // Apenas jogadores podem usar?
    public abstract void execute(CommandSender sender, String[] args); // Lógica do comando

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList(); // Override para auto-completação
    }
}