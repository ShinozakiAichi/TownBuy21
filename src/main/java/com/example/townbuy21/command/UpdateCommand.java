package com.example.townbuy21.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.example.townbuy21.TownBuy21;

/**
 * Synchronises all towns with Towny.
 */
public class UpdateCommand extends BaseCommand {

    public UpdateCommand(TownBuy21 plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.syncTownsWithTowny();
        sender.sendMessage("Towns synchronised with Towny");
        return true;
    }
}
