package me.zyypj.booter.minecraft.spigot.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class GenericEconomy {

    private static Economy economy = null;

    public GenericEconomy() {
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = (rsp == null) ? null : rsp.getProvider();
    }

    public static boolean isVaultEnabled() {
        return economy != null;
    }

    public static double getBalance(OfflinePlayer player) {
        return isVaultEnabled() ? economy.getBalance(player) : 0.0;
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {
        return isVaultEnabled() && economy.has(player, amount);
    }

    public static boolean withdraw(OfflinePlayer player, double amount) {
        return isVaultEnabled() && economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public static boolean deposit(OfflinePlayer player, double amount) {
        return isVaultEnabled() && economy.depositPlayer(player, amount).transactionSuccess();
    }

    public static String getCurrencyName() {
        return isVaultEnabled() ? economy.currencyNameSingular() : " real";
    }

    public static String getCurrencyPlural() {
        return isVaultEnabled() ? economy.currencyNamePlural() : " reais";
    }

    public static boolean hasBankSupport() {
        return isVaultEnabled() && economy.hasBankSupport();
    }

    public static String format(double amount) {
        return isVaultEnabled() ? economy.format(amount) : String.valueOf(amount);
    }
}
