package com.github.sirblobman.plugin.economy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.util.StringUtil;

import com.github.sirblobman.api.plugin.ConfigurablePlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class EconomyPlugin extends ConfigurablePlugin {
    private Economy economyHandler;

    public EconomyPlugin() {
        this.economyHandler = null;
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        BasicEconomy basicEconomy = new BasicEconomy(this);
        ServicesManager servicesManager = Bukkit.getServicesManager();
        servicesManager.register(Economy.class, basicEconomy, this, ServicePriority.Lowest);
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName();
        List<String> playerList = Arrays.asList("balance", "eco-set", "eco-give", "eco-take");
        List<String> amountList = Arrays.asList("eco-set", "eco-give", "eco-take");

        if (args.length == 1 && playerList.contains(commandName)) {
            Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
            List<String> playerNameList = onlinePlayerCollection.stream().map(Player::getName)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[0], playerNameList, new ArrayList<>());
        }

        if (args.length == 2 && amountList.contains(commandName)) {
            return Collections.singletonList("1.0");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        switch (commandName) {
            case "balance": return commandBalance(sender, args);
            case "eco-set": return commandSet(sender, args);
            case "eco-give": return commandGive(sender, args);
            case "eco-take": return commandTake(sender, args);
            default: break;
        }

        return false;
    }

    private @NotNull Economy getEconomyHandler() {
        if (this.economyHandler != null) {
            return this.economyHandler;
        }

        ServicesManager servicesManager = Bukkit.getServicesManager();
        RegisteredServiceProvider<Economy> registration = servicesManager.getRegistration(Economy.class);
        if (registration == null) {
            this.economyHandler = new BasicEconomy(this);
            return this.economyHandler;
        }

        this.economyHandler = registration.getProvider();
        if (this.economyHandler == null) {
            this.economyHandler = new BasicEconomy(this);
        }

        return this.economyHandler;
    }

    private boolean commandBalance(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target = (sender instanceof Player ? (Player) sender : null);
        if (args.length > 1) {
            String targetName = args[0];
            target = Bukkit.getPlayerExact(targetName);
        }

        if (target == null) {
            sender.sendMessage("Missing/invalid target parameter.");
            return false;
        }

        Economy economyHandler = getEconomyHandler();
        double balance = economyHandler.getBalance(target);
        sender.sendMessage("Balance of " + target.getName() + ": " + economyHandler.format(balance));
        return true;
    }

    private boolean commandSet(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            return false;
        }

        String targetName = args[0];
        String amountString = args[1];
        double amount;

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage("Unknown player '" + targetName + "'.");
            return false;
        }

        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException ex) {
            sender.sendMessage("Invalid number '" + amountString + "'.");
            return false;
        }

        Economy economyHandler = getEconomyHandler();
        double oldBalance = economyHandler.getBalance(target);
        EconomyResponse economyResponse1 = economyHandler.withdrawPlayer(target, oldBalance);
        if (!economyResponse1.transactionSuccess()) {
            sender.sendMessage("Failed to change balance. Reason: " + economyResponse1.errorMessage);
            return true;
        }

        EconomyResponse economyResponse2 = economyHandler.depositPlayer(target, amount);
        if (!economyResponse2.transactionSuccess()) {
            sender.sendMessage("Failed to change balance. Reason: " + economyResponse1.errorMessage);
            return true;
        }

        sender.sendMessage("Old Balance: " + economyHandler.format(oldBalance));
        sender.sendMessage("Set balance of " + target.getName() + " to " + economyHandler.format(amount));
        return true;
    }

    private boolean commandGive(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            return false;
        }

        String targetName = args[0];
        String amountString = args[1];
        double amount;

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage("Unknown player '" + targetName + "'.");
            return false;
        }

        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException ex) {
            sender.sendMessage("Invalid number '" + amountString + "'.");
            return false;
        }

        Economy economyHandler = getEconomyHandler();
        double oldBalance = economyHandler.getBalance(target);

        EconomyResponse response = economyHandler.depositPlayer(target, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage("Failed to change balance. Reason: " + response.errorMessage);
            return true;
        }

        double newBalance = economyHandler.getBalance(target);
        sender.sendMessage("Old Balance: " + economyHandler.format(oldBalance));
        sender.sendMessage("Set balance of " + target.getName() + " to " + economyHandler.format(newBalance));
        return true;
    }

    private boolean commandTake(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            return false;
        }

        String targetName = args[0];
        String amountString = args[1];
        double amount;

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage("Unknown player '" + targetName + "'.");
            return false;
        }

        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException ex) {
            sender.sendMessage("Invalid number '" + amountString + "'.");
            return false;
        }

        Economy economyHandler = getEconomyHandler();
        double oldBalance = economyHandler.getBalance(target);

        EconomyResponse response = economyHandler.withdrawPlayer(target, amount);
        if (!response.transactionSuccess()) {
            sender.sendMessage("Failed to change balance. Reason: " + response.errorMessage);
            return true;
        }

        double newBalance = economyHandler.getBalance(target);
        sender.sendMessage("Old Balance: " + economyHandler.format(oldBalance));
        sender.sendMessage("Set balance of " + target.getName() + " to " + economyHandler.format(newBalance));
        return true;
    }
}
