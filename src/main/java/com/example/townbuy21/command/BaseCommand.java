package com.example.townbuy21.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.example.townbuy21.TownBuy21;

/**
 * Base implementation for plugin commands providing access to the main plugin
 * instance and a default empty tab completion.
 */
public abstract class BaseCommand implements TabExecutor {

    protected final TownBuy21 plugin;

    protected BaseCommand(TownBuy21 plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
