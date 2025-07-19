package com.example.townbuy21;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.example.townbuy21.config.MenuConfig;
import com.example.townbuy21.db.TownRepository;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

public class MenuManager implements Listener {

    private final TownBuy21 plugin;
    private final Map<UUID, Integer> pages = new HashMap<>();
    private final Map<UUID, Map<Integer, String>> townSlots = new HashMap<>();

    public MenuManager(TownBuy21 plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, int page) {
        plugin.syncTownsWithTowny();
        MenuConfig menu = plugin.getMenuConfig();
        int size = menu.getRows() * 9;
        int townsPerPage = size - 9;
        TownRepository repo = plugin.getRepository();
        List<String> towns = repo.getBuyableTowns();
        int totalPages = (int) Math.ceil(towns.size() / (double) townsPerPage);
        if (totalPages <= 0) totalPages = 1;
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory inv = Bukkit.createInventory(null, size, menu.getTitle().replace("%page%", String.valueOf(page + 1)));
        ItemStack filler = menu.getFillerItem();
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }

        int start = page * townsPerPage;
        int end = Math.min(start + townsPerPage, towns.size());
        int slot = 0;
        Map<Integer, String> slotMap = new HashMap<>();
        for (int i = start; i < end; i++) {
            String name = towns.get(i);
            Town town = TownyAPI.getInstance().getTown(name);
            if (town == null) continue;
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%town%", town.getName());
            placeholders.put("%price%", String.valueOf(repo.getPrice(town.getName())));
            String nation = "";
            try { nation = town.getNationOrNull() != null ? town.getNationOrNull().getName() : ""; } catch (Exception ignore) {}
            placeholders.put("%nation%", nation);
            placeholders.put("%chunks%", String.valueOf(town.getTownBlocks().size()));
            ItemStack item = menu.createTownItem(placeholders);
            if (slot < townsPerPage) {
                inv.setItem(slot, item);
                slotMap.put(slot, name);
            }
            slot++;
        }

        int prevSlot = menu.getPrevPageSlot(size);
        int nextSlot = menu.getNextPageSlot(size);
        if (page > 0) {
            inv.setItem(prevSlot, menu.getPrevPageItem());
        }
        if (page < totalPages - 1) {
            inv.setItem(nextSlot, menu.getNextPageItem());
        }

        player.openInventory(inv);
        pages.put(player.getUniqueId(), page);
        townSlots.put(player.getUniqueId(), slotMap);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Integer page = pages.get(player.getUniqueId());
        if (page == null) return;
        event.setCancelled(true);
        MenuConfig menu = plugin.getMenuConfig();
        int size = menu.getRows() * 9;
        int slot = event.getSlot();
        if (slot == menu.getNextPageSlot(size)) {
            openMenu(player, page + 1);
            return;
        } else if (slot == menu.getPrevPageSlot(size)) {
            openMenu(player, page - 1);
            return;
        }
        Map<Integer, String> slots = townSlots.get(player.getUniqueId());
        if (slots != null && slots.containsKey(slot)) {
            String townName = slots.get(slot);
            player.closeInventory();
            Bukkit.dispatchCommand(player, "townbuy " + townName);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        pages.remove(id);
        townSlots.remove(id);
    }
}
