package com.example.townbuy21.handler;

import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public interface BuyHandler {
    boolean isBuyable(Town town);
    boolean buy(Resident resident, Town town) throws TownyException;
    default void transferTown(Town town, Resident newMayor) throws TownyException {
        town.forceSetMayor(newMayor);
        town.save();
    }
}
