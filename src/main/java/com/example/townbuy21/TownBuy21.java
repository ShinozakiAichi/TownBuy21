package com.example.townbuy21;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.example.townbuy21.config.LanguageConfig;
import com.example.townbuy21.config.TownsConfig;
import com.example.townbuy21.config.MenuConfig;
import com.example.townbuy21.db.MysqlManager;
import com.example.townbuy21.db.TownRepository;
import com.example.townbuy21.handler.BuyHandler;
import com.example.townbuy21.handler.EconomyHandler;
import com.example.townbuy21.handler.ItemHandler;
import com.example.townbuy21.service.TownService;
import org.bukkit.Material;
import com.example.townbuy21.command.impl.NpcTownsCommand;
import com.example.townbuy21.command.impl.TbMenuCommand;
import com.example.townbuy21.MenuManager;
import com.example.townbuy21.command.impl.TownBuyCommand;
import com.example.townbuy21.command.impl.TownSellCommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownySettings;


public class TownBuy21 extends JavaPlugin implements Listener {

    private static TownBuy21 instance;
    private LanguageConfig language;
    private TownsConfig towns;
    private BuyHandler handler;
    private MysqlManager mysql;
    private TownRepository repo;
    private TownService townService;
    private MenuConfig menuConfig;
    private MenuManager menuManager;
    private final Set<String> npcTowns = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        language = new LanguageConfig(this);
        mysql = new MysqlManager(this);
        try {
            mysql.connect();
        } catch (java.sql.SQLException e) {
            getLogger().severe("MySQL connection failed: " + e.getMessage());
            return;
        }
        repo = new TownRepository(mysql.getConnection());
        towns = new TownsConfig(this, repo);
        menuConfig = new MenuConfig(this);
        menuManager = new MenuManager(this);

        if ("MATERIAL".equalsIgnoreCase(towns.getMode())) {
            handler = new ItemHandler(repo, Material.EMERALD);
        } else {
            handler = new EconomyHandler(repo);
        }
        townService = new TownService(handler, repo);

        syncTownsWithTowny();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(menuManager, this);
        getCommand("townbuy").setExecutor(new TownBuyCommand(this));
        getCommand("townsell").setExecutor(new TownSellCommand(this));
        getCommand("npctowns").setExecutor(new NpcTownsCommand(this));
        getCommand("tbmenu").setExecutor(new TbMenuCommand(this));
    }

    @Override
    public void onDisable() {
        if (mysql != null) {
            mysql.close();
        }
    }


    public static TownBuy21 getInstance() {
        return instance;
    }

    public LanguageConfig getLanguage() {
        return language;
    }

    public TownsConfig getTownsConfig() {
        return towns;
    }

    public BuyHandler getBuyHandler() {
        return handler;
    }

    public TownService getTownService() {
        return townService;
    }

    public MenuConfig getMenuConfig() {
        return menuConfig;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public TownRepository getRepository() {
        return repo;
    }
    public Collection<String> getNpcTowns() {
        return Set.copyOf(npcTowns);
    }

    public void addNpcTown(String name) {
        npcTowns.add(name);
    }

    public void removeNpcTown(String name) {
        npcTowns.remove(name);
    }

    /**
     * Updates the internal NPC town cache and synchronises all towns from
     * Towny with the database.
     */
    public void syncTownsWithTowny() {
        npcTowns.clear();
        TownyAPI.getInstance().getTowns().forEach(t -> {
            String name = t.getName();
            String mayor = t.getMayor().getName();
            boolean npc = mayor.matches("NPC\\d+");
            if (npc) {
                npcTowns.add(name);
            }
            double price = repo.getPrice(name);
            if (npc) {
                price = 10.0; // base price for NPC towns
            }
            try {
                repo.saveTown(name, mayor, npc, price);
            } catch (java.sql.SQLException e) {
                getLogger().warning("Could not save town " + name + ": " + e.getMessage());
            }
        });
    }

    @EventHandler
    public void onPreDeleteTown(PreDeleteTownEvent event) {
        Resident mayor = event.getTown().getMayor();
        if (mayor != null && !mayor.getName().matches("NPC\\d+")) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(this, () -> {
                try {
                    townService.sellToNpc(mayor);
                } catch (com.palmergames.bukkit.towny.exceptions.TownyException e) {
                    getLogger().warning(e.getMessage());
                }
            });
        }
    }

    /**
     * Towny 0.99 removed Resident#getNPCResident().
     * This helper fetches the NPC resident using the configured account name.
     */
    public static Resident getNPCResident() {
        String npcName = "NPC";
        try {
            Method m = TownySettings.class.getMethod("getNPCAccountName");
            npcName = (String) m.invoke(null);
        } catch (Exception e) {
            try {
                Method m = TownySettings.class.getMethod("getString", String.class);
                npcName = (String) m.invoke(null, "npc_account_name");
            } catch (Exception ignore) {
                // Fall back to default NPC name
            }
        }
        return TownyAPI.getInstance().getResident(npcName);
    }
}
