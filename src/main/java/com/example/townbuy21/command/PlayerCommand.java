package com.example.townbuy21.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.example.townbuy21.TownBuy21;

/**
 * Base command that requires the sender to be a player. If the command is
 * executed by the console, a simple message is sent and the call ends.
 */
public abstract class PlayerCommand extends BaseCommand {

    protected PlayerCommand(TownBuy21 plugin) {
        super(plugin);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only");
            return true;
        }
        return onCommand(player, command, label, args);
    }

    /**
     * Handles the command for a player sender.
     */
    protected abstract boolean onCommand(Player player, Command command, String label, String[] args);
}
