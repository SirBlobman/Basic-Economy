package com.github.sirblobman.plugin.economy;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public final class PlayerDataManager {
    private final EconomyPlugin plugin;
    private final Map<UUID, YamlConfiguration> configurationMap;

    public PlayerDataManager(@NotNull EconomyPlugin plugin) {
        this.plugin = plugin;
        this.configurationMap = new ConcurrentHashMap<>();
    }

    private @NotNull EconomyPlugin getPlugin() {
        return this.plugin;
    }

    private @NotNull Logger getLogger() {
        return getPlugin().getLogger();
    }

    /**
     * Fetch the data configuration for the specified player.
     * If the player does not have any data, the configuration will be empty.
     *
     * @param player The player who owns the configuration
     * @return A configuration from memory. If the configuration is not in memory it will be loaded from a file.
     */
    public @NotNull YamlConfiguration get(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        YamlConfiguration configuration = this.configurationMap.getOrDefault(playerId, null);
        if (configuration != null) {
            return configuration;
        }

        reload(player);
        return get(player);
    }

    /**
     * Saves a player configuration to a file
     *
     * @param player The player who owns the configuration
     */
    public void save(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        YamlConfiguration configuration = this.configurationMap.getOrDefault(playerId, null);
        if (configuration == null) {
            return;
        }

        try {
            File file = getFile(player);
            configuration.save(file);
        } catch (IOException ex) {
            Logger logger = getLogger();
            String logMessage = "Failed to save data for player '" + playerId + "':";
            logger.log(Level.WARNING, logMessage, ex);
        }
    }

    /**
     * Reloads a player configuration from a file into memory
     *
     * @param player The player who owns the configuration
     */
    public void reload(OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        try {
            YamlConfiguration configuration = load(player);
            this.configurationMap.put(playerId, configuration);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger logger = getLogger();
            String logMessage = "Failed to load data for player '" + playerId + "':";
            logger.log(Level.WARNING, logMessage, ex);
        }
    }

    private File getFile(@NotNull OfflinePlayer player) {
        EconomyPlugin plugin = getPlugin();
        File dataFolder = plugin.getDataFolder();
        File playerDataFolder = new File(dataFolder, "playerdata");

        UUID playerId = player.getUniqueId();
        String playerIdString = playerId.toString();
        String fileName = (playerIdString + ".data.yml");
        return new File(playerDataFolder, fileName);
    }

    private @NotNull YamlConfiguration load(@NotNull OfflinePlayer player)
            throws IOException, InvalidConfigurationException {
        File playerFile = getFile(player);
        if (!playerFile.exists()) {
            return new YamlConfiguration();
        }

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(playerFile);
        return configuration;
    }
}
