package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private String name;
    private Inventory actualInventory;
    private List<Trade> tradeList = new ArrayList<>();

    public Shop(String name) {
        this.name = name;
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

    private static ItemStack createValidateItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "validate");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }
}
