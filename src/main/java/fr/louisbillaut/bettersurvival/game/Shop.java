package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.utils.Head;
import fr.louisbillaut.bettersurvival.utils.Messages;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private String name;
    private Inventory actualInventory;
    private List<Trade> tradeList = new ArrayList<>();
    private Location location;

    public Shop(String name) {
        this.name = name;
        createShopInventory();
    }
    public Shop() {
    }
    public void createShopInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Shop configuration " + name);

        for (int slot = 0; slot < 27; slot++) {
            if(slot == 11) {
                continue;
            }
            inventory.setItem(slot, createGlassBlock());
        }

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

    public void createCustomVillager(Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setGravity(false);

        List<MerchantRecipe> recipes = new ArrayList<>();
        for (Trade trade : tradeList) {
            MerchantRecipe merchantRecipe = new MerchantRecipe(trade.getItemsToBuy(), Integer.MAX_VALUE);
            for (ItemStack item : trade.getItemsToExchange()) {
                merchantRecipe.addIngredient(item);
            }
            recipes.add(merchantRecipe);
        }
        villager.setRecipes(recipes);
        this.location = location;
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

    public void setActualInventory(Inventory actualInventory) {
        this.actualInventory = actualInventory;
    }

    public Inventory getActualInventory() {
        return actualInventory;
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

    public void displayTrades(Player player) {
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
        for(Trade trade: tradeList) {
            ItemStack glassBlock = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glassBlock.getItemMeta();
            glassMeta.setDisplayName(ChatColor.GREEN + "Trade " + ((i-2) / 9));
            glassBlock.setItemMeta(glassMeta);
            inventory.setItem(i - 2, glassBlock);
            inventory.setItem(i, trade.getItemsToBuy());
            inventory.setItem(i + 1, getArrowRight());
            if(trade.getItemsToExchange().size() > 0) {
                inventory.setItem(i + 2, trade.getItemsToExchange().get(0));
            }
            if(trade.getItemsToExchange().size() > 1) {
                inventory.setItem(i + 3, trade.getItemsToExchange().get(1));
            }

            inventory.setItem(i + 5, createRemoveItem());
            i += 9;
        }

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

    private ItemStack getArrowRight() {
        ItemStack villagerHead = Head.getCustomHead(Head.quartzArrowRight);
        ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
        villagerHeadMeta.setDisplayName(" ");
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
}
