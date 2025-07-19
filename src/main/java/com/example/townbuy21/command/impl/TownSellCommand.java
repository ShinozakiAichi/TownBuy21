package com.example.townbuy21.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.example.townbuy21.TownBuy21;
import com.example.townbuy21.command.PlayerCommand;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class TownSellCommand extends PlayerCommand {

    public TownSellCommand(TownBuy21 plugin) {
        super(plugin);
    }

    @Override
    protected boolean onCommand(Player player, Command command, String label, String[] args) {
        plugin.syncTownsWithTowny();

        Resident res = TownyAPI.getInstance().getResident(player);
        if (res == null || !res.isMayor()) {
            player.sendMessage(plugin.getLanguage().get("is_not_mayor"));
            return true;
        }

        Confirmation.runOnAccept(() -> {
            try {
                plugin.getTownService().sellToNpc(res);
            } catch (TownyException e) {
                player.sendMessage(e.getMessage());
            }
        }).sendTo(player);
        return true;
    }
}
