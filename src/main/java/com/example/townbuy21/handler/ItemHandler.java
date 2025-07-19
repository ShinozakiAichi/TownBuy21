package com.example.townbuy21.handler;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.example.townbuy21.db.TownRepository;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Handler that charges a player with a material cost for buying a town.
 */
public class ItemHandler extends AbstractBuyHandler {

    private final Material material;

    public ItemHandler(TownRepository repo, Material material) {
        super(repo);
        this.material = material;
    }

    @Override
    public boolean buy(Resident resident, Town town) throws TownyException {
        Player player = resident.getPlayer();
        if (player == null) {
            throw new TownyException("Player must be online to buy a town");
        }
        int amount = (int) Math.round(repo.getPrice(town.getName()));
        ItemStack stack = new ItemStack(material, amount);
        if (!player.getInventory().containsAtLeast(stack, amount)) {
            return false;
        }
        player.getInventory().removeItem(stack);
        return true;
    }
}
