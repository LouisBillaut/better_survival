package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Detector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;

public class Game implements Serializable {
    private final int maxRadiusPlot = 5;
    private List<Player> players;
    public Map<UUID, ArrayList<ArmorStand>> armorStands = new HashMap<>();
    public Map<UUID, ArrayList<BukkitRunnable>> armorStandsRotationTasks = new HashMap<>();

    public BsBucks bs = new BsBucks();
    public Game() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayer(org.bukkit.entity.Player bukkitPlayer) {
        for (Player player : players) {
            if (player.getPlayerName().equals(bukkitPlayer.getDisplayName())) {
                return player;
            }
        }
        return null;
    }

    public Player getPlayerFromShop(Shop shop) {
        for(Player p: players) {
            for(Shop s: p.getShops()) {
                if (s.equals(shop)) return p;
            }
        }

        return null;
    }

    public boolean isLocationInOtherPlayerPlot(org.bukkit.entity.Player player, Location location) {
        for(Player p: players) {
            if(p.getPlayerName().equals(player.getDisplayName())) {
                continue;
            }
            for(Plot plot: p.getPlots()) {
                if (Detector.isInZone(location, plot.getLocation1(), plot.getLocation2(), plot.getHeight())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isLocationInPlot(Location location) {
        for(Player p: players) {
            for(Plot plot: p.getPlots()) {
                if (Detector.isInZone(location, plot.getLocation1(), plot.getLocation2(), plot.getHeight())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isLocationInPlotWithRadius(org.bukkit.entity.Player player, Location location) {
        for(Player p: players) {
            if(player.getDisplayName().equals(p.getPlayerName())) {
                for(Plot plot: p.getPlots()) {
                    if (Detector.isInZone(location, plot.getLocation1(), plot.getLocation2(), plot.getHeight())) {
                        player.sendMessage(ChatColor.RED + "You can't select in your plots");
                        return true;
                    }
                }
                continue;
            }
            for(Plot plot: p.getPlots()) {
                if (Detector.isInZoneWithRadius(location, plot.getLocation1(), plot.getLocation2(), plot.getHeight(), maxRadiusPlot)) {
                    player.sendMessage(ChatColor.RED + "You are too close from an existing plot (in it or 5 blocks radius)");
                    return true;
                }
            }
        }

        return false;
    }

    public List<Shop> getAllShops() {
        List<Shop> res = new ArrayList<>();
        for(Player p: players) {
            res.addAll(p.getShops());
        }

        return res;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void displayBsBucksInventoryToPlayer(Main instance, org.bukkit.entity.Player player) {
        int page = 0;
        if(player.hasMetadata("bsBucksPage")) {
            for (MetadataValue v: player.getMetadata("bsBucksPage")) {
                page = v.asInt();
            }
        }

        if (page*5 > bs.getItemsToSale().size()) {
            page = page -1;
            player.setMetadata("bsBucksPage", new FixedMetadataValue(instance, page));
        }
        if(page < 0) {
            page = 0;
            player.setMetadata("bsBucksPage", new FixedMetadataValue(instance, page));
        }
        Inventory inventory = Bukkit.createInventory(null, 54, "bsBuck market");
        for(var i= 0; i < 54; i++) {
            inventory.setItem(i, createGlassBlock());
        }

        int i = 2;
        for(var index= 0; index < bs.getItemsToSale().size() && index <= 4 && (page * 5 + index) < bs.getItemsToSale().size(); index++) {
            int p = page * 5 + index;
            inventory.setItem(i, bs.getItemsToSale().get(p).getItem());
            inventory.setItem(i + 1, Shop.getArrowRight());
            inventory.setItem(i + 2, getBsPrice(bs.getItemsToSale().get(p).getPrice()));
            inventory.setItem(i + 4, createBuyItem());
            i += 9;
        }

        inventory.setItem(45, Shop.getPreviousArrow());
        inventory.setItem(53, Shop.getNextArrow());
        player.openInventory(inventory);
    }

    private ItemStack createBuyItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "buy");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private ItemStack getBsPrice(int number) {
        ItemStack gold = new ItemStack(Material.RAW_GOLD);
        ItemMeta goldMeta = gold.getItemMeta();
        goldMeta.setDisplayName(ChatColor.GOLD + "" + number);
        gold.setItemMeta(goldMeta);

        return gold;
    }

    public void loadFromConfig(Main instance, ConfigurationSection config) {
        if (config.contains("players")) {
            ConfigurationSection playersSection = config.getConfigurationSection("players");
            for (String playerKey : Objects.requireNonNull(playersSection).getKeys(false)) {
                ConfigurationSection playerConfig = playersSection.getConfigurationSection(playerKey);

                Player player = new Player(Objects.requireNonNull(playerConfig).getString("name"));
                player.loadFromConfig(instance, playersSection);
                players.add(player);
            }
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        ConfigurationSection playersSection = config.createSection("players");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            ConfigurationSection playerConfig = playersSection.createSection(String.valueOf(i));
            player.saveToConfig(playerConfig);
        }
    }

    public void cancelAllVillagersTasks() {
        players.forEach(p -> p.getShops().forEach(Shop::cancelVillagerTask));
    }
}
