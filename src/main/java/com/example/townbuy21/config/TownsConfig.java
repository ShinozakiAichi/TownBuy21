package com.example.townbuy21.config;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.example.townbuy21.db.TownRepository;

import com.palmergames.bukkit.towny.object.Town;

public class TownsConfig {
    private final JavaPlugin plugin;
    private final TownRepository repo;
    private FileConfiguration config;

    public TownsConfig(JavaPlugin plugin, TownRepository repo) {
        this.plugin = plugin;
        this.repo = repo;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "towns.yml");
        if (!file.exists()) {
            plugin.saveResource("towns.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection towns = config.getConfigurationSection("towns");
        if (towns != null) {
            for (String name : towns.getKeys(false)) {
                ConfigurationSection t = towns.getConfigurationSection(name);
                if (t == null) continue;
                String mayor = t.getString("mayor", "NPC");
                boolean buyable = t.getBoolean("buyable", false);
                double price = t.getDouble("price", 0.0);
                try {
                    repo.saveTown(name, mayor, buyable, price);
                } catch (java.sql.SQLException e) {
                    plugin.getLogger().warning("Could not save town " + name + ": " + e.getMessage());
                }
            }
        }
    }

    public double getPrice(Town town) {
        if (town == null) return 0.0;
        return repo.getPrice(town.getName());
    }

    public boolean isBuyable(Town town) {
        if (town == null) return false;
        return repo.isBuyable(town.getName());
    }

    public String getMode() {
        return config.getString("mode", "ECONOMY").toUpperCase();
    }

    public TownRepository getRepository() {
        return repo;
    }
}
