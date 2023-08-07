package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.animations.Animation;
import fr.louisbillaut.bettersurvival.pets.Pet;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.util.*;

import static fr.louisbillaut.bettersurvival.game.Cosmetics.renamePetPrice;
import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;
import static fr.louisbillaut.bettersurvival.listeners.PlayerListener.computeNumberOfBlocks;

public class Player {
    private Cosmetics cosmetics = new Cosmetics();
    private List<Plot> plots;
    private List<Shop> shops = new ArrayList<>();
    private List<ItemStack> claims = new ArrayList<>();
    private Map<String, Long> lastWelcomeTimes = new HashMap<>();
    private Compass compass = new Compass();
    private String playerName;
    private static int maxShops = 5;

    private int bsBucks = 30000;

    private LocalDate lastLoginDate = LocalDate.now();
    private int consecutiveLoginDays = 0;

    private static final Map<Integer, Integer> REWARD_MAPPING = new HashMap<>();

    private BukkitTask teleportRunnable;

    private long playedTime = 0;

    private int deaths = 0;

    private fr.louisbillaut.bettersurvival.game.Scoreboard customScoreboard;

    static {
        REWARD_MAPPING.put(2, 100);
        REWARD_MAPPING.put(3, 120);
        REWARD_MAPPING.put(4, 130);
        REWARD_MAPPING.put(5, 150);
    }

    private org.bukkit.entity.Player bukkitPlayer;

    public Player(Main instance, Game game, String playerName) {
        this.playerName = playerName;
        plots = new ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                bukkitPlayer = p;
            }
        }
        customScoreboard = new fr.louisbillaut.bettersurvival.game.Scoreboard(instance, this);
    }

    public Scoreboard getCustomScoreboard() {
        return customScoreboard;
    }

    public void login() {
        LocalDate currentDate = LocalDate.now();

        if (lastLoginDate != null && currentDate.equals(lastLoginDate.plusDays(1))) {
            consecutiveLoginDays++;
        } else {
            consecutiveLoginDays = 1;
        }

        var key = consecutiveLoginDays;
        if(key > 5) {
            key = 5;
        }
        if (REWARD_MAPPING.containsKey(key)) {
            int reward = REWARD_MAPPING.get(key);
            bsBucks += reward;
            if(bukkitPlayer != null) {
                sendRewardMessage(bukkitPlayer, consecutiveLoginDays);
            }
        }

        lastLoginDate = currentDate;
    }

    public long getPlayedTime() {
        if (bukkitPlayer != null) {
            var ticksPlayed = bukkitPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
            playedTime = ticksPlayed / (20 * 60);
        }
        return playedTime;
    }

    public int getDeaths() {
        if (bukkitPlayer != null) {
            deaths = bukkitPlayer.getStatistic(Statistic.DEATHS);
        }
        return deaths;
    }

    public Cosmetics getCosmetics() {
        return cosmetics;
    }

    public void sendRewardMessage(org.bukkit.entity.Player player, int consecutiveLoginDays) {
        var currentRewardKey = consecutiveLoginDays;
        if (currentRewardKey > 5) {
            currentRewardKey = 5;
        }
        int currentReward = REWARD_MAPPING.get(currentRewardKey);
        int nextReward = 0;

        var key = consecutiveLoginDays + 1;
        if(key > 5) {
            key = 5;
        }
        if (REWARD_MAPPING.containsKey(key)) {
            nextReward = REWARD_MAPPING.get(key);
        }

        String message = ChatColor.GREEN + "Congratulations! You have received a reward for logging in " + consecutiveLoginDays + " day(s).";
        message += "\n" + ChatColor.GRAY + "You received " + currentReward + " bsBucks.";

        if (nextReward > 0) {
            message += "\n" + ChatColor.GRAY + "Your next reward will be " + nextReward + " bsBucks.";
        }

        player.sendMessage(message);
        Location fireworkLocation = player.getLocation().clone();
        fireworkLocation.setY(fireworkLocation.getY() + 5);
        Firework firework = (Firework) player.getLocation().getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.GREEN)
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
    }

    public void setBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Plot getPlot(String name) {
        for(Plot p: plots) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    public void setTeleportRunnable(BukkitTask teleportRunnable) {
        this.teleportRunnable = teleportRunnable;
    }

    public BukkitTask getTeleportRunnable() {
        return teleportRunnable;
    }

    public void addBsBucks(int bucks) {
        bsBucks += bucks;
    }

    public void removeBsBuck(int bucks) {
        bsBucks -= bucks;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void addPlot(Plot zone) {
        plots.add(zone);
    }

    public void addShop(Shop shop) {
        shops.add(shop);
    }

    public static int getMaxShops() {
        return maxShops;
    }

    public void addTarget(Main instance, org.bukkit.entity.Player player, Location location, String name) {
        compass.addTarget(instance, player, location, name);
    }
    public void clearCompass() {
        compass.clear(bukkitPlayer);
    }

    public void sendWelcomeTitle(org.bukkit.entity.Player player, String playerName, String plotName) {
        long currentTime = System.currentTimeMillis();

        String uniqueKey = plotName;
        int minuteDelay = 2 * 60000;
        if (!lastWelcomeTimes.containsKey(uniqueKey) || currentTime - lastWelcomeTimes.get(uniqueKey) >= minuteDelay) {
            String title = ChatColor.GREEN + Main.sendLocalizedMessage("welcome");
            String subtitle = plotName + " " + Main.sendLocalizedMessage("of") + " " + playerName;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
            player.sendTitle(title, subtitle, 10, 70, 20);
            lastWelcomeTimes.put(uniqueKey, currentTime);
        }
    }

    public Shop getShop(String name) {
        for(Shop s: shops) {
            if(s.getName().equals(name)) {
                return s;
            }
        }

        return null;
    }

    public void showBsBuck() {
        if(bukkitPlayer == null) return;
        bukkitPlayer.sendMessage(ChatColor.GREEN + "you have " + ChatColor.GOLD + bsBucks + " bsBucks");
    }

    public void createChestWithClaims(Location location) {
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        BlockState blockState = block.getState();
        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;
            Inventory inventory = chest.getBlockInventory();

            ItemStack[] itemsArray = new ItemStack[inventory.getSize()];
            for (int i = 0; i < Math.min(claims.size(), itemsArray.length); i++) {
                itemsArray[i] = claims.get(i);
            }

            inventory.setContents(itemsArray);
            claims.subList(0, Math.min(claims.size(), itemsArray.length)).clear();
        }
    }

    public boolean hasPlotWithName(String name) {
        for(Plot p: plots) {
            if(p.getName().equals(name)) return true;
        }

        return false;
    }

    public boolean hasShopWithName(String name) {
        for(Shop s: shops) {
            if(s.getName().equals(name)) return true;
        }

        return false;
    }

    public int getBsBucks() {
        return bsBucks;
    }

    public int getTotalBlocks() {
        int total = 0;
        for(Plot p: plots) {
            total += computeNumberOfBlocks(p.getLocation1(), p.getLocation2(), p.getHeight());
        }

        return total;
    }

    public int getTotalEstimatedFortune() {
        int bsInPlots = 0;
        int nbOfBlocInPlots = 0;
        double increasedPricePercent = BsBucks.blockPrice;
        for (Plot p: plots) {
            var computedBlocks = computeNumberOfBlocks(p.getLocation1(), p.getLocation2(), p.getHeight());
            bsInPlots += computedBlocks * increasedPricePercent;
            if (nbOfBlocInPlots + computedBlocks >= BsBucks.PlotNbOfBlocToIncreasePrice) {
                int nbOfPlotOfPlateau = (int) ((nbOfBlocInPlots + computedBlocks) / BsBucks.PlotNbOfBlocToIncreasePrice);
                int remainingBlocks = (int) ((nbOfBlocInPlots + computedBlocks) % BsBucks.PlotNbOfBlocToIncreasePrice);
                for (var i = 0; i < nbOfPlotOfPlateau; i++) {
                    increasedPricePercent += increasedPricePercent * BsBucks.PercentageAugmentation;
                }
                nbOfBlocInPlots = remainingBlocks;
            }
        }

        int petsPrice = 0;
        for (Pet p: cosmetics.getPets()) {
            petsPrice += p.getPrice();
        }
        int animationsPrice = 0;
        for (Animation a: cosmetics.getAnimations()) {
            animationsPrice += a.getPrice();
        }
        int renamePrices = cosmetics.getRenamesNumber() * renamePetPrice;
        return bsInPlots + bsBucks + petsPrice + animationsPrice + renamePrices;
    }

    public void setBsBucks(int bsBucks) {
        this.bsBucks = bsBucks;
    }

    public List<ItemStack> getClaims() {
        return claims;
    }

    public void displayClaims() {
        if(bukkitPlayer == null) return;
        Inventory inventory = Bukkit.createInventory(null, 54, "claims list");
        for(var i = 0; i < claims.size() && i < 54; i++) {
            inventory.addItem(claims.get(i));
        }
        bukkitPlayer.openInventory(inventory);
    }

    public void addClaimsToShop(Trade trade) {
        claims.addAll(trade.getItemsToExchange());
    }

    public void removeShop(String name) {
        for(var i = 0; i < shops.size(); i ++) {
            if(shops.get(i).getName().equals(name)){
                shops.remove(i);
            }
        }
    }

    public void removePlot(Plot zone) {
        plots.remove(zone);
    }

    public List<Plot> getPlots() {
        return plots;
    }

    public org.bukkit.entity.Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public ConfigurationSection getPlayerConfigByName(String playerName, ConfigurationSection playersSection) {
        for (String playerKey : playersSection.getKeys(false)) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(playerKey);
            String name = playerSection.getString("name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return playerSection;
            }
        }
        return null;
    }

    public void loadFromConfig(Main instance, ConfigurationSection config) {
        ConfigurationSection c = getPlayerConfigByName(this.playerName, config);
        if (c == null) {
            return;
        }
        if (c.contains("deaths")) {
            this.deaths = c.getInt("deaths");
        }
        if (c.contains("playedTime")) {
            this.playedTime = c.getInt("playedTime");
        }
        if (c.contains("bsBucks")) {
            this.bsBucks = c.getInt("bsBucks");
        }
        if (c.contains("lastLoginDate")) {
            this.lastLoginDate = LocalDate.parse(c.getString("lastLoginDate"));
        }

        if (c.contains("consecutiveLoginDays")) {
            this.consecutiveLoginDays = c.getInt("consecutiveLoginDays");
        }
        if (c.contains("plots")) {
            for (String plotKey : Objects.requireNonNull(c.getConfigurationSection("plots")).getKeys(false)) {
                Plot plot = new Plot();
                plot.loadFromConfig(Objects.requireNonNull(c.getConfigurationSection("plots." + plotKey)));
                plots.add(plot);
            }
        }
        if (c.contains("shops")) {
            ConfigurationSection shopsSection = c.getConfigurationSection("shops");
            for (String shopKey : shopsSection.getKeys(false)) {
                Shop shop = new Shop();
                shop.loadFromConfig(instance, Objects.requireNonNull(shopsSection.getConfigurationSection(shopKey)));
                shops.add(shop);
            }
        }
        if (c.contains("claims")) {
            List<?> claimsList = c.getList("claims");
            for (Object claimObject : claimsList) {
                if (claimObject instanceof ItemStack) {
                    ItemStack claimItem = (ItemStack) claimObject;
                    claims.add(claimItem);
                }
            }
        }

        if (c.contains("cosmetics")) {
            ConfigurationSection cosmeticsSection = c.getConfigurationSection("cosmetics");
            cosmetics = new Cosmetics();
            cosmetics.loadFromConfig(instance, cosmeticsSection);
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("name", playerName);
        config.set("lastLoginDate", lastLoginDate.toString());
        config.set("consecutiveLoginDays", consecutiveLoginDays);

        ConfigurationSection plotsSection = config.createSection("plots");
        for (int i = 0; i < plots.size(); i++) {
            Plot plot = plots.get(i);
            ConfigurationSection plotSection = plotsSection.createSection(String.valueOf(i));
            plotSection.set("name", plot.getName());
            plot.saveToConfig(config.createSection("plots." + i));
        }

        ConfigurationSection shopsSection = config.createSection("shops");
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
            ConfigurationSection shopSection = shopsSection.createSection(String.valueOf(i));
            shop.saveToConfig(shopSection);
        }

        ConfigurationSection cosmeticsSection = config.createSection("cosmetics");
        cosmetics.saveToConfig(cosmeticsSection);

        config.set("claims", claims);
        config.set("bsBucks", bsBucks);
        config.set("playedTime", playedTime);
        config.set("deaths", deaths);
    }

    public void displayAllTrades(Main instance, Game game) {
        org.bukkit.entity.Player player = getBukkitPlayer();
        List<Shop> shops = new ArrayList<>();
        List<Trade> tradeList = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        for(Player p: game.getPlayers()) {
            for(Shop s: p.getShops()) {
                for(Trade t: s.getTradeList()) {
                    shops.add(s);
                    tradeList.add(t);
                    players.add(p);
                }
            }
        }

        int page = 0;
        if(player.hasMetadata("allTradeListPage")) {
            for (MetadataValue v: player.getMetadata("allTradeListPage")) {
                page = v.asInt();
            }
        }

        if (page + 5 > tradeList.size()) {
            page = page -1;
            player.setMetadata("allTradeListPage", new FixedMetadataValue(instance, page));
        }
        if(page < 0) {
            page = 0;
            player.setMetadata("allTradeListPage", new FixedMetadataValue(instance, page));
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "all trades");
        for(var i= 0; i < 54; i++) {
            inventory.setItem(i, createGlassBlock());
        }

        int i = 2;
        for(var index= 0; index < tradeList.size() && index <= 4 && (page * 5 + index) < tradeList.size(); index++) {
            int p = page * 5 + index;
            ItemStack glassBlock = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glassBlock.getItemMeta();
            glassMeta.setDisplayName(ChatColor.GREEN + "Max trades: " +tradeList.get(p).getMaxTrade());
            glassBlock.setItemMeta(glassMeta);
            inventory.setItem(i - 2, glassBlock);
            inventory.setItem(i+3, tradeList.get(p).getItemsToBuy());
            inventory.setItem(i + 2, Shop.getArrowRight());
            if(tradeList.get(p).getItemsToExchange().size() > 0) {
                inventory.setItem(i, tradeList.get(p).getItemsToExchange().get(0));
            }
            if(tradeList.get(p).getItemsToExchange().size() > 1) {
                inventory.setItem(i + 1, tradeList.get(p).getItemsToExchange().get(1));
            }
            inventory.setItem(i + 5, getShopVillagerHead(players.get(p).getPlayerName(), shops.get(p).getName(), shops.get(p).getLocation()));

            i += 9;
        }

        inventory.setItem(45, Shop.getPreviousArrow());
        inventory.setItem(53, Shop.getNextArrow());

        player.openInventory(inventory);
    }

    private ItemStack getShopVillagerHead(String playerName, String name, Location location) {
        ItemStack villagerHead = Head.getCustomHead(Head.villagerHead);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(ChatColor.LIGHT_PURPLE  + playerName + ": " + ChatColor.GREEN + name);
        villagerHead.setItemMeta(villagerHeadMeta);
        if(location != null) {
            Shop.addLocationToItemStack(villagerHead, location);
        }

        return villagerHead;
    }

    public void displayListShopInventory() {
        int inventorySize = Math.max(9, (int) Math.ceil(shops.size() / 9.0) * 9);
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Shops List");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneMeta = glassPane.getItemMeta();
        glassPaneMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassPaneMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
            inventory.setItem(inventorySize - 9 + i, glassPane);
        }

        for (int i = 0; i < shops.size(); i++) {
            ItemStack villagerHead = Head.getCustomHead(Head.villagerHead);
            ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
            villagerHeadMeta.setDisplayName(ChatColor.GREEN + shops.get(i).getName());
            villagerHead.setItemMeta(villagerHeadMeta);
            int row = i / 9;
            int column = i % 9;

            inventory.setItem(row * 9 + column, villagerHead);
        }

        bukkitPlayer.openInventory(inventory);
    }

    public void displayListPlotInventory() {
        int inventorySize = Math.max(9, (int) Math.ceil(plots.size() / 9.0) * 9);
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Plots List");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneMeta = glassPane.getItemMeta();
        glassPaneMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassPaneMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
            inventory.setItem(inventorySize - 9 + i, glassPane);
        }

        for (int i = 0; i < plots.size(); i++) {
            Plot plot = plots.get(i);

            ItemStack grassBlock = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta grassBlockMeta = grassBlock.getItemMeta();
            grassBlockMeta.setDisplayName(ChatColor.GREEN + "Plot " + plot.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Location1: X: " + plot.getLocation1().getX() +
                    " Y: " + plot.getLocation1().getY() +
                    " Z: " + plot.getLocation1().getZ());
            lore.add(ChatColor.YELLOW + "Location2: X: " + plot.getLocation2().getX() +
                    " Y: " + plot.getLocation2().getY() +
                    " Z: " + plot.getLocation2().getZ());
            lore.add(ChatColor.YELLOW + "Height: " + plot.getHeight());

            grassBlockMeta.setLore(lore);
            grassBlock.setItemMeta(grassBlockMeta);

            int row = i / 9;
            int column = i % 9;

            inventory.setItem(row * 9 + column, grassBlock);
        }

        bukkitPlayer.openInventory(inventory);
    }

}
