package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import fr.louisbillaut.bettersurvival.utils.Messages;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private String name;
    private Inventory actualInventory;
    private int actualNumberOfTrade = 1;
    private ItemStack itemToBuy;
    private List<Trade> tradeList = new ArrayList<>();
    private Location location;
    private Villager villager;
    private static int MaxTradeLimit = 8;

    public Shop(String name) {
        this.name = name;
        createShopInventory();
    }
    public Shop() {
    }
    public void createShopInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Shop configuration " + name);

        itemToBuy = null;
        actualNumberOfTrade = 1;
        for (int slot = 0; slot < 27; slot++) {
            if(slot == 12) {
                continue;
            }
            inventory.setItem(slot, createGlassBlock());
        }

        inventory.setItem(11, createGreenGlassBlock(1));
        inventory.setItem(2, createPlusHeadItem());
        inventory.setItem(20, createMinusHeadItem());

        ItemStack itemToExchangePage1 = createPageItem("Item to exchange 1");
        inventory.setItem(14, itemToExchangePage1);

        ItemStack itemToExchangePage2 = createPageItem("Item to exchange 2");
        inventory.setItem(15, itemToExchangePage2);

        ItemStack cancel = createCancelItem();
        inventory.setItem(18, cancel);

        ItemStack validate = createValidateItem();
        inventory.setItem(26, validate);

        actualInventory = inventory;
    }

    private List<MerchantRecipe> getMerchantRecipesFromTrades() {
        List<MerchantRecipe> recipes = new ArrayList<>();
        for (Trade trade : tradeList) {
            MerchantRecipe merchantRecipe = new MerchantRecipe(trade.getItemsToBuy(), Integer.MAX_VALUE);
            for (ItemStack item : trade.getItemsToExchange()) {
                merchantRecipe.addIngredient(item);
            }
            recipes.add(merchantRecipe);
        }

        return recipes;
    }

    public void createCustomVillager(Location location) {
        if(villager != null) {
            villager.remove();
        }
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setSilent(true);

        List<MerchantRecipe> recipes = getMerchantRecipesFromTrades();

        villager.setRecipes(recipes);
        this.location = location;
        this.villager = villager;
    }

    public void removeItem(Player player, int slot) {
        int itemSlot = slot - 9;
        actualInventory.setItem(slot, createGlassBlock());
        String pageName = "Item to exchange 1";
        if(itemSlot == 15) pageName = "Item to exchange 2";
        actualInventory.setItem(itemSlot, createPageItem(pageName));
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
    }

    public void sendAddItemCommand(Player player) {
        player.sendMessage(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "To add item: /shop add <shop name> <item> <quantity>");
        player.spigot().sendMessage(
                Messages.createClickableMessage(ChatColor.GOLD + "[add item]", "/shop add " + name + " ")
        );
        player.closeInventory();
    }

    public void addTrade(Trade trade) {
        tradeList.add(trade);
        if(villager != null) {
            villager.setRecipes(getMerchantRecipesFromTrades());
        }
    }
    public void removeTrade(int i) {
        tradeList.remove(i);
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public String getName() {
        return name;
    }

    public Villager getVillager() {
        return villager;
    }

    public Location getLocation() {
        return location;
    }

    public static int getMaxTradeLimit() {
        return MaxTradeLimit;
    }

    public void setActualInventory(Inventory actualInventory) {
        this.actualInventory = actualInventory;
    }

    public ItemStack getItemToBuy() {
        return itemToBuy;
    }

    public void setItemToBuy(ItemStack itemToBuy) {
        this.actualInventory.setItem(12, itemToBuy);
        this.itemToBuy = itemToBuy;
    }

    public Inventory getActualInventory() {
        return actualInventory;
    }

    public Trade getTradeByItemToBuy(List<ItemStack> items, ItemStack item) {
        for (Trade t: tradeList) {
            boolean contains = true;
            for(ItemStack i: items) {
                if(!t.itemsToExchangeContainsMaterial(i.getType())) contains = false;
            }
            if (!contains) continue;
            if(t.getItemsToBuy().getType().equals(item.getType())) return t;
        }

        return null;
    }

    public int incrementActualNumberOfTrade() {
        actualNumberOfTrade++;
        this.actualInventory.setItem(11, createGreenGlassBlock(actualNumberOfTrade));
        return actualNumberOfTrade;
    }

    public int decrementActualNumberOfTrade() {
        actualNumberOfTrade--;
        this.actualInventory.setItem(11, createGreenGlassBlock(actualNumberOfTrade));
        return actualNumberOfTrade;
    }

    public int getActualNumberOfTrade() {
        return actualNumberOfTrade;
    }

    public void setActualNumberOfTrade(int actualNumberOfTrade) {
        this.actualNumberOfTrade = actualNumberOfTrade;
        this.actualInventory.setItem(11, createGreenGlassBlock(actualNumberOfTrade));
    }

    public boolean addItemToShop(Player player, ItemStack itemStack) {
        if(actualInventory == null) return false;
        ItemStack remove = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = remove.getItemMeta();
        removeMeta.setDisplayName(ChatColor.RED + "remove");
        remove.setItemMeta(removeMeta);
        if (actualInventory.getItem(14).getItemMeta().getDisplayName().contains("Item to exchange")) {
            actualInventory.setItem(14, itemStack);
            actualInventory.setItem(23, remove);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.openInventory(actualInventory);
            return true;
        }
        if (actualInventory.getItem(15).getItemMeta().getDisplayName().contains("Item to exchange")) {
            actualInventory.setItem(15, itemStack);
            actualInventory.setItem(24, remove);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.openInventory(actualInventory);
            return true;
        }
        player.openInventory(actualInventory);
        return false;
    }

    public void displayTrades(Main instance, Player player) {
        var tradesToRemove = new ArrayList<Trade>();
        for(Trade trade: tradeList) {
            if (trade.getMaxTrade() <= 0) {
                tradesToRemove.add(trade);
            }
        }
        for(Trade t: tradesToRemove) {
            tradeList.remove(t);
        }

        int page = 0;
        if(player.hasMetadata("tradeListPage")) {
            for (MetadataValue v: player.getMetadata("tradeListPage")) {
                page = v.asInt();
            }
        }

        if (page + 4 > tradeList.size()) {
            page = page -1;
            player.setMetadata("tradeListPage", new FixedMetadataValue(instance, page));
        }
        if(page < 0) {
            page = 0;
            player.setMetadata("tradeListPage", new FixedMetadataValue(instance, page));
        }
        if((page+1)* 4 > getMaxTradeLimit()) {
            page = page -1;
            player.setMetadata("tradeListPage", new FixedMetadataValue(instance, page));
        }
        Inventory inventory = Bukkit.createInventory(null, 54, name + " trades list");
        for(var i= 0; i < 54; i++) {
            inventory.setItem(i, createGlassBlock());
        }

        ItemStack villagerHead = getShopVillagerHead();
        villagerHead = addLocationToItemStack(villagerHead, location);
        inventory.setItem(0, villagerHead);

        inventory.setItem( 4, createNewTradeItem());
        ItemStack world = Head.getCustomHead(Head.world);
        ItemMeta worldMeta = world.getItemMeta();
        worldMeta.setDisplayName(ChatColor.GREEN + "place");
        world.setItemMeta(worldMeta);
        inventory.setItem(6, world);

        int i = 11;
        for(var index= 0; index < tradeList.size() && index <= 3 && (page * 4 + index) < tradeList.size(); index++) {
            int p = page * 4 + index;
            ItemStack glassBlock = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glassBlock.getItemMeta();
            glassMeta.setDisplayName(ChatColor.GREEN + "Trade " + (p + 1));
            if (glassMeta.hasLore()) {
                glassMeta.getLore().add(ChatColor.GRAY + "Max trade: " + tradeList.get(p).getMaxTrade());
            } else {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Max trade: " + tradeList.get(p).getMaxTrade());
                glassMeta.setLore(lore);
            }
            glassBlock.setItemMeta(glassMeta);
            inventory.setItem(i - 2, glassBlock);
            inventory.setItem(i, tradeList.get(p).getItemsToBuy());
            inventory.setItem(i + 1, getArrowRight());
            if(tradeList.get(p).getItemsToExchange().size() > 0) {
                inventory.setItem(i + 2, tradeList.get(p).getItemsToExchange().get(0));
            }
            if(tradeList.get(p).getItemsToExchange().size() > 1) {
                inventory.setItem(i + 3, tradeList.get(p).getItemsToExchange().get(1));
            }

            inventory.setItem(i + 5, createRemoveItem());
            i += 9;
        }

        inventory.setItem(45, getPreviousArrow());
        inventory.setItem(53, getNextArrow());

        player.openInventory(inventory);
    }

    private ItemStack addLocationToItemStack(ItemStack itemStack, Location location) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            String field = "Location";
            String locationDescription = "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();

            if (itemMeta.hasLore()) {
                itemMeta.getLore().add(ChatColor.GRAY + field + ": " + locationDescription);
            } else {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + field + ": " + locationDescription);
                itemMeta.setLore(lore);
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public void loadFromConfig(ConfigurationSection config) {
        if (config.contains("name")) {
            name = config.getString("name");
        }
        if (config.contains("location")) {
            location = config.getLocation("location");
        }
        if (config.contains("trades")) {
            ConfigurationSection tradeSection = config.getConfigurationSection("trades");
            tradeList.clear();
            for (String key : tradeSection.getKeys(false)) {
                ConfigurationSection tradeConfig = tradeSection.getConfigurationSection(key);
                Trade trade = new Trade();
                trade.loadFromConfig(tradeConfig);
                tradeList.add(trade);
            }
        }

        createShopInventory();
        villager = getVillagerAtLocation(location, name);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setSilent(true);
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("name", name);
        config.set("location", location);
        ConfigurationSection tradeSection = config.createSection("trades");
        for (int i = 0; i < tradeList.size(); i++) {
            ConfigurationSection tradeConfig = tradeSection.createSection(String.valueOf(i));
            Trade trade = tradeList.get(i);
            trade.saveToConfig(tradeConfig);
        }
    }

    public Villager getVillagerAtLocation(Location location, String name) {
        World world = location.getWorld();
        if (world != null) {
            for (Entity entity : world.getNearbyEntities(location, 2, 2, 2)) {
                if (entity.getType() == EntityType.VILLAGER && entity instanceof Villager) {
                    Villager villager = (Villager) entity;
                    if (villager.getCustomName() != null && villager.getCustomName().equals(name)) {
                        return villager;
                    }
                }
            }
        }
        return null;
    }

    private ItemStack getArrowRight() {
        ItemStack villagerHead = Head.getCustomHead(Head.quartzArrowRight);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(" ");
        villagerHead.setItemMeta(villagerHeadMeta);

        return villagerHead;
    }

    public ItemStack getNextArrow() {
        ItemStack villagerHead = Head.getCustomHead(Head.quartzArrowRight);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(ChatColor.GREEN + "next");
        villagerHead.setItemMeta(villagerHeadMeta);

        return villagerHead;
    }

    public ItemStack getPreviousArrow() {
        ItemStack villagerHead = Head.getCustomHead(Head.quartzArrowLeft);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(ChatColor.GREEN + "previous");
        villagerHead.setItemMeta(villagerHeadMeta);

        return villagerHead;
    }

    private ItemStack getShopVillagerHead() {
        ItemStack villagerHead = Head.getCustomHead(Head.villagerHead);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(ChatColor.GREEN + name);
        villagerHead.setItemMeta(villagerHeadMeta);

        return villagerHead;
    }

    private static ItemStack createGlassBlock() {
        ItemStack glassBlock = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassBlock.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassBlock.setItemMeta(glassMeta);
        return glassBlock;
    }

    public static ItemStack createGreenGlassBlock(int number) {
        ItemStack glassBlock = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassBlock.getItemMeta();
        glassMeta.setDisplayName("Max trades: " + ChatColor.GREEN + number);
        glassBlock.setItemMeta(glassMeta);
        return glassBlock;
    }

    private static ItemStack createPageItem(String pageName) {
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(pageName);
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createCancelItem() {
        ItemStack pageItem = new ItemStack(Material.BARRIER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.RED + "cancel");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createRemoveItem() {
        ItemStack pageItem = new ItemStack(Material.BARRIER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.RED + "remove");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createValidateItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "validate");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createNewTradeItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "add trade");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createPlusHeadItem() {
        ItemStack plus = Head.getCustomHead(Head.quartzPlus);
        ItemMeta plusMeta = plus.getItemMeta();
        plusMeta.setDisplayName(ChatColor.GREEN + "+");
        plus.setItemMeta(plusMeta);
        return plus;
    }

    private static ItemStack createMinusHeadItem() {
        ItemStack minus = Head.getCustomHead(Head.quartzMinus);
        ItemMeta minusMeta = minus.getItemMeta();
        minusMeta.setDisplayName(ChatColor.GREEN + "-");
        minus.setItemMeta(minusMeta);
        return minus;
    }
}
