package com.example.townbuy21.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.example.townbuy21.TownBuy21;

/**
 * Reloads plugin configuration and language files.
 */
public class ReloadCommand extends BaseCommand {

    public ReloadCommand(TownBuy21 plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        plugin.getLanguage().reload();
        plugin.getTownsConfig().reload();
        plugin.getMenuConfig().reload();
        sender.sendMessage("Configs reloaded");
        return true;
    }
}
