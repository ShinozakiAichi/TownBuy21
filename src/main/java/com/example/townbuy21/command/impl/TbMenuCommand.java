package com.example.townbuy21.command.impl;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.example.townbuy21.MenuManager;
import com.example.townbuy21.TownBuy21;
import com.example.townbuy21.command.PlayerCommand;

/**
 * Opens a GUI with all towns that can be purchased.
 */
public class TbMenuCommand extends PlayerCommand {

    private final MenuManager menuManager;

    public TbMenuCommand(TownBuy21 plugin) {
        super(plugin);
        this.menuManager = plugin.getMenuManager();
    }

    @Override
    protected boolean onCommand(Player player, Command command, String label, String[] args) {
        int page = parsePage(args);
        menuManager.openMenu(player, page);
        return true;
    }

    private int parsePage(String[] args) {
        if (args.length == 0) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(args[0]) - 1);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }
}
