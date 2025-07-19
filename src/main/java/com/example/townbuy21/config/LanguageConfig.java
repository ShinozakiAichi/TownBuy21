package com.example.townbuy21.config;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public LanguageConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "language.yml");
        if (!file.exists()) {
            plugin.saveResource("language.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        String val = config.getString("language." + path, path);
        return ChatColor.translateAlternateColorCodes('&', val);
    }
}
