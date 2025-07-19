package com.example.townbuy21.handler;

import com.example.townbuy21.db.TownRepository;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Base implementation for different buying strategies. It stores the
 * {@link TownRepository} and provides a common {@link #isBuyable(Town)}
 * implementation.
 */
public abstract class AbstractBuyHandler implements BuyHandler {

    protected final TownRepository repo;

    protected AbstractBuyHandler(TownRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean isBuyable(Town town) {
        return town != null && repo.isBuyable(town.getName());
    }
}
