package com.example.townbuy21.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.example.townbuy21.command.BaseCommand;
import com.example.townbuy21.command.ReloadCommand;
import com.example.townbuy21.command.UpdateCommand;
import com.example.townbuy21.TownBuy21;
import org.bukkit.entity.Player;

import com.example.townbuy21.handler.BuyHandler;
import com.example.townbuy21.db.TownRepository;
import com.example.townbuy21.service.TownService;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class TownBuyCommand extends BaseCommand {

    private final Map<String, BaseCommand> subCommands = new HashMap<>();

    public TownBuyCommand(TownBuy21 plugin) {
        super(plugin);
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("update", new UpdateCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.isOp()) {
            BaseCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                return sub.onCommand(sender, command, label, new String[0]);
            }
        }

        plugin.syncTownsWithTowny();

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getLanguage().get("not_enough_args"));
            return true;
        }

        return handlePurchase(player, args[0]);
    }

    private boolean handlePurchase(Player player, String townName) {
        Town town = TownyAPI.getInstance().getTown(townName);
        if (town == null) {
            player.sendMessage(plugin.getLanguage().get("unknown_town"));
            return true;
        }

        BuyHandler handler = plugin.getBuyHandler();
        if (!handler.isBuyable(town)) {
            player.sendMessage(plugin.getLanguage().get("no"));
            return true;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident != null && resident.isMayor()) {
            player.sendMessage(plugin.getLanguage().get("already_mayor"));
            return true;
        }

        try {
            if (plugin.getTownService().buyTown(resident, town)) {
                player.sendMessage(plugin.getLanguage()
                        .get("successful_purchase").replace("%town%", town.getName()));
            }
        } catch (TownyException e) {
            player.sendMessage(e.getMessage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        String arg = args[0].toLowerCase();

        if (sender.isOp()) {
            subCommands.keySet().stream()
                    .filter(name -> name.startsWith(arg))
                    .forEach(completions::add);
        }

        TownRepository repo = plugin.getTownsConfig().getRepository();
        completions.addAll(repo.getBuyableTowns().stream()
                .filter(name -> name.toLowerCase().startsWith(arg))
                .collect(Collectors.toList()));

        return completions;
    }
}
