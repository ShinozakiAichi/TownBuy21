package com.example.townbuy21.service;

import org.bukkit.Bukkit;

import com.example.townbuy21.TownBuy21;
import com.example.townbuy21.db.TownRepository;
import com.example.townbuy21.handler.BuyHandler;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Service that encapsulates the logic of buying and selling towns.
 */
public class TownService {

    private final BuyHandler handler;
    private final TownRepository repo;

    public TownService(BuyHandler handler, TownRepository repo) {
        this.handler = handler;
        this.repo = repo;
    }

    /**
     * Attempts to buy the given town for the resident.
     *
     * @return true if the purchase and transfer were successful
     */
    public boolean buyTown(Resident resident, Town town) throws TownyException {
        if (!handler.isBuyable(town) || !handler.buy(resident, town)) {
            return false;
        }
        handler.transferTown(town, resident);
        try {
            double price = repo.getPrice(town.getName());
            repo.saveTown(town.getName(), resident.getName(), false, price);
        } catch (java.sql.SQLException e) {
            TownBuy21.getInstance().getLogger()
                    .warning("Could not update town: " + e.getMessage());
        }
        TownBuy21 plugin = TownBuy21.getInstance();
        plugin.removeNpcTown(town.getName());
        plugin.syncTownsWithTowny();
        return true;
    }

    /**
     * Transfers the mayor's town to an NPC account and kicks all residents.
     */
    public void sellToNpc(Resident mayor) throws TownyException {
        Town town = mayor.getTown();
        String townName = town.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "townyadmin set mayor " + townName + " npc");
        TownBuy21 plugin = TownBuy21.getInstance();
        plugin.addNpcTown(townName);
        String npcName = "NPC";
        if (TownBuy21.getNPCResident() != null) {
            npcName = TownBuy21.getNPCResident().getName();
        }
        try {
            repo.saveTown(townName, npcName, true, 10.0);
        } catch (java.sql.SQLException e) {
            plugin.getLogger().warning("Could not update town: " + e.getMessage());
        }
        plugin.syncTownsWithTowny();
        town.getResidents().forEach(r -> {
            String name = r.getName();
            if (!name.matches("NPC\\d+")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "ta town " + townName + " kick " + name);
            }
        });
    }
}
