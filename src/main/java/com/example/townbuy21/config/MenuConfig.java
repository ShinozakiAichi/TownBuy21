package com.example.townbuy21.config;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

/**
 * Loads GUI menu configuration from tbgui.yml and provides helpers to
 * create item stacks used inside the menu.
 */
public class MenuConfig {

    private final Plugin plugin;
    private FileConfiguration config;

    public MenuConfig(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "tbgui.yml");
        if (!file.exists()) {
            plugin.saveResource("tbgui.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getTitle() {
        String val = config.getString("title", "Menu");
        return ChatColor.translateAlternateColorCodes('&', val);
    }

    public int getRows() {
        return config.getInt("rows", 1);
    }

    private ItemStack createBaseItem(ConfigurationSection sec) {
        String iaId = sec.getString("itemsadder", "");
        if (!iaId.isEmpty() && Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            try {
                Class<?> c = Class.forName("dev.lone.itemsadder.api.CustomStack");
                Object stack = c.getMethod("getInstance", String.class).invoke(null, iaId);
                if (stack != null) {
                    ItemStack item = (ItemStack) c.getMethod("getItemStack").invoke(stack);
                    if (item != null) return item;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load ItemsAdder item " + iaId + ": " + e.getMessage());
            }
        }
        Material mat = Material.matchMaterial(sec.getString("material", "STONE"));
        if (mat == null) mat = Material.STONE;
        return new ItemStack(mat);
    }

    public ItemStack createTownItem(Map<String, String> placeholders) {
        ConfigurationSection sec = config.getConfigurationSection("town-item");
        if (sec == null) return new ItemStack(Material.EMERALD);
        ItemStack item = createBaseItem(sec);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = sec.getString("name", "%town%");
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                name = name.replace(e.getKey(), e.getValue());
            }
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> lore = sec.getStringList("lore").stream().map(s -> {
                for (Map.Entry<String, String> e : placeholders.entrySet()) {
                    s = s.replace(e.getKey(), e.getValue());
                }
                return ChatColor.translateAlternateColorCodes('&', s);
            }).collect(Collectors.toList());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getFillerItem() {
        ConfigurationSection sec = config.getConfigurationSection("filler");
        if (sec == null) return new ItemStack(Material.AIR);
        return createBaseItem(sec);
    }

    public ItemStack getNextPageItem() {
        ConfigurationSection sec = config.getConfigurationSection("next-page");
        if (sec == null) return new ItemStack(Material.ARROW);
        return createBaseItem(sec);
    }

    public ItemStack getPrevPageItem() {
        ConfigurationSection sec = config.getConfigurationSection("prev-page");
        if (sec == null) return new ItemStack(Material.ARROW);
        return createBaseItem(sec);
    }

    private int getRelativeSlot(ConfigurationSection sec, int def, int size) {
        int slot = sec.getInt("slot", def);
        if (slot < 0) slot = size + slot;
        if (slot < 0 || slot >= size) {
            return def < 0 ? size + def : def;
        }
        return slot;
    }

    /**
     * Returns the slot for the next page button. Negative numbers are
     * interpreted relative to the end of the inventory.
     */
    public int getNextPageSlot(int size) {
        ConfigurationSection sec = config.getConfigurationSection("next-page");
        return sec == null ? size - 1 : getRelativeSlot(sec, -1, size);
    }

    /**
     * Returns the slot for the previous page button. Negative numbers are
     * interpreted relative to the end of the inventory.
     */
    public int getPrevPageSlot(int size) {
        ConfigurationSection sec = config.getConfigurationSection("prev-page");
        return sec == null ? size - 9 : getRelativeSlot(sec, -9, size);
    }
}
