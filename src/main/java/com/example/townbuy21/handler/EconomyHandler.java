package com.example.townbuy21.handler;

import org.bukkit.Bukkit;

import com.example.townbuy21.db.TownRepository;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;


public class EconomyHandler extends AbstractBuyHandler {

    public EconomyHandler(TownRepository repo) {
        super(repo);
    }

    @Override
    public boolean buy(Resident resident, Town town) throws TownyException {
        double price = repo.getPrice(town.getName());
        String playerName = resident.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "economy take " + playerName + " " + price);
        return true;
    }

    @Override
    public void transferTown(Town town, Resident newMayor) throws TownyException {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "townyadmin set mayor " + town.getName() + " " + newMayor.getName());
    }
}
