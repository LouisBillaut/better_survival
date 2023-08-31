package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.animations.Animation;
import fr.louisbillaut.bettersurvival.npcs.Dave;
import fr.louisbillaut.bettersurvival.pets.Pet;
import fr.louisbillaut.bettersurvival.pets.Wolf;
import fr.louisbillaut.bettersurvival.utils.Detector;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
    private final Map<org.bukkit.entity.Player, Pet> pets = new HashMap<>();
    private final Map<org.bukkit.entity.Player, Animation> animations = new HashMap<>();
    private final Map<World, Integer> sleepingPlayersCount = new HashMap<>();
    private final double sleepPercentageThreshold = 0.5;

    public BsBucks bs = new BsBucks();
    private LeaderBoard leaderBoard = new LeaderBoard();

    private EasterEgg easterEgg = new EasterEgg();
    public Game() {
        players = new ArrayList<>();
    }

    public double getSleepPercentageThreshold() {
        return sleepPercentageThreshold;
    }

    public Map<World, Integer> getSleepingPlayersCount() {
        return sleepingPlayersCount;
    }

    public void putSleepingPlayer(World world, Integer integer) {
        sleepingPlayersCount.put(world, integer);
    }

    public void removeSleepingPlayer(World world) {
        sleepingPlayersCount.remove(world);
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

    public Player getPlayerByName(String name) {
        for (Player p: players) {
            if (p.getPlayerName().equals(name)) return p;
        }

        return null;
    }

    public EasterEgg getEasterEgg() {
        return easterEgg;
    }

    public Player getPlayerFromShop(Shop shop) {
        for(Player p: players) {
            for(Shop s: p.getShops()) {
                if (s.equals(shop)) return p;
            }
        }

        return null;
    }

    public Map<org.bukkit.entity.Player, Pet> getPets() {
        return pets;
    }

    public Map<org.bukkit.entity.Player, Animation> getAnimations() {
        return animations;
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
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

    public BsBucks getBs() {
        return bs;
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
            inventory.setItem(i + 4, createSellItem());
            inventory.setItem(i + 5, createSellAllItem());
            i += 9;
        }

        inventory.setItem(45, Shop.getPreviousArrow());
        inventory.setItem(53, Shop.getNextArrow());
        player.openInventory(inventory);
    }

    private ItemStack createSellItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "sell");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private ItemStack createSellAllItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        pageMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pageMeta.setDisplayName(ChatColor.GREEN + "sell all");

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

    private List<Player> getPlayersWithoutSettingPlayer() {
        List<Player> clonedList = new ArrayList<>();

        for (Player player : players) {
            if (!player.getPlayerName().equalsIgnoreCase("betterSurvival")) {
                clonedList.add(player);
            }
        }

        return clonedList;
    }
    public List<Player> getSortedPlayersByTotalDeaths() {
        var clonedList = getPlayersWithoutSettingPlayer();
        Collections.sort(clonedList, Comparator.comparingInt(Player::getDeaths));
        Collections.reverse(clonedList);
        return clonedList;
    }

    public List<Player> getSortedPlayersByTotalBlocks() {
        var clonedList = getPlayersWithoutSettingPlayer();
        Collections.sort(clonedList, Comparator.comparingInt(Player::getTotalBlocks));
        Collections.reverse(clonedList);
        return clonedList;
    }

    public List<Player> getSortedPlayersByBsBucks() {
        var clonedList = getPlayersWithoutSettingPlayer();
        Collections.sort(clonedList, Comparator.comparingInt(Player::getTotalEstimatedFortune));
        Collections.reverse(clonedList);
        return clonedList;
    }

    public List<Player> getSortedPlayersByPlayedTime() {
        var clonedList = getPlayersWithoutSettingPlayer();
        Collections.sort(clonedList, Comparator.comparingLong(Player::getPlayedTime));
        Collections.reverse(clonedList);
        return clonedList;
    }

    public void loadFromConfig(Main instance, Game game, ConfigurationSection config) {
        if (config.contains("players")) {
            ConfigurationSection playersSection = config.getConfigurationSection("players");
            for (String playerKey : Objects.requireNonNull(playersSection).getKeys(false)) {
                ConfigurationSection playerConfig = playersSection.getConfigurationSection(playerKey);

                Player player = new Player(instance, game, Objects.requireNonNull(playerConfig).getString("name"));
                player.loadFromConfig(instance, playersSection);
                players.add(player);
            }
        }
        if (config.contains("leaderboard")) {
            ConfigurationSection leaderBoardSection = config.getConfigurationSection("leaderboard");
            leaderBoard.loadFromConfig(instance, this, leaderBoardSection);
        }
        if (config.contains("easterEgg")) {
            ConfigurationSection easterEggSection = config.getConfigurationSection("easterEgg");
            easterEgg.loadFromConfig(instance, this, easterEggSection);
        }

        if (easterEgg.getDaveLocation() != null) {
            Dave.spawn(easterEgg.getDaveLocation());
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        ConfigurationSection playersSection = config.createSection("players");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            ConfigurationSection playerConfig = playersSection.createSection(String.valueOf(i));
            player.saveToConfig(playerConfig);
        }
        ConfigurationSection leaderBoardSection = config.createSection("leaderboard");
        leaderBoard.saveToConfig(leaderBoardSection);
        ConfigurationSection easterEggSection = config.createSection("easterEgg");
        easterEgg.saveToConfig(easterEggSection);
    }

    public void cancelAllVillagersTasks() {
        players.forEach(p -> p.getShops().forEach(Shop::cancelVillagerTask));
    }
    public void deleteAllVillagers(){
        players.forEach(p -> p.getShops().forEach(Shop::removeVillager));

        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()){
                if (entity instanceof Villager){
                    Villager villager = (Villager) entity;
                    if (villager.isCustomNameVisible() && villager.getCustomName() != null){
                        villager.remove();
                    }
                }
            }
        });
    }

    public void removePet(org.bukkit.entity.Player player) {
        Pet pet = pets.remove(player);
        if (pet != null) {
            pet.despawn();
        }
    }

    public void removeAnimation(org.bukkit.entity.Player player) {
        var animation = animations.remove(player);
        if (animation != null) {
            animation.stopAnimation();
        }
    }
}
