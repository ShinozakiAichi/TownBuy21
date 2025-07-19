package com.example.townbuy21.command.impl;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.example.townbuy21.TownBuy21;
import com.example.townbuy21.command.BaseCommand;

public class NpcTownsCommand extends BaseCommand {

    public NpcTownsCommand(TownBuy21 plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.syncTownsWithTowny();
        Collection<String> towns = plugin.getNpcTowns();
        if (towns.isEmpty()) {
            sender.sendMessage("No NPC towns found.");
            return true;
        }
        sender.sendMessage("NPC towns:");
        towns.forEach(sender::sendMessage);
        return true;
    }
}

