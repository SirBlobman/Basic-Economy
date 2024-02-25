package com.github.sirblobman.plugin.economy;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public final class BasicEconomy extends AbstractEconomy {
    private final EconomyPlugin plugin;

    public BasicEconomy(@NotNull EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    private @NotNull EconomyPlugin getPlugin() {
        return this.plugin;
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        EconomyPlugin plugin = getPlugin();
        return plugin.getPlayerDataManager();
    }

    @SuppressWarnings("deprecation")
    private @NotNull OfflinePlayer getPlayer(@NotNull String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    private void setBalance(@NotNull String playerName, double balance) {
        OfflinePlayer player = getPlayer(playerName);
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("balance", balance);
        playerDataManager.save(player);
    }

    @Override
    public boolean isEnabled() {
        EconomyPlugin plugin = getPlugin();
        return plugin.isEnabled();
    }

    @Override
    public @NotNull String getName() {
        EconomyPlugin plugin = getPlugin();
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public @NotNull String format(double amount) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat format = new DecimalFormat("$#,##0.00", symbols);
        return format.format(amount);
    }

    @Override
    public @NotNull String currencyNamePlural() {
        return "$";
    }

    @Override
    public @NotNull String currencyNameSingular() {
        return "$";
    }

    @Override
    public boolean hasAccount(@NotNull String playerName) {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = getPlayer(playerName);
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        return playerData.getDouble("balance", 0.0D);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        double balance = getBalance(playerName);
        return (balance >= amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance = getBalance(playerName);
        if (balance >= amount) {
            double newBalance = (balance - amount);
            setBalance(playerName, newBalance);
            return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(amount, balance, ResponseType.FAILURE, "Not enough money.");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance = getBalance(playerName);
        double newBalance = (balance + amount);
        setBalance(playerName, newBalance);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.FAILURE, "Not Implemented!");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }
}
