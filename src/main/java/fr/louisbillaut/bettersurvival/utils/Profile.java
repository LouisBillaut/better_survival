package fr.louisbillaut.bettersurvival.utils;

import fr.louisbillaut.bettersurvival.game.LeaderBoard;
import fr.louisbillaut.bettersurvival.game.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static fr.louisbillaut.bettersurvival.animations.Animation.createAnimationItem;
import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;
import static fr.louisbillaut.bettersurvival.pets.Pet.backItem;
import static fr.louisbillaut.bettersurvival.pets.Pet.createPetItem;

public class Profile {
    public static void displayProfileInventory(Player player) {
        if (player.getBukkitPlayer() == null) return;
        Inventory inventory = Bukkit.createInventory(null, 27, "Profile");

        for (int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var playerHead = getPlayerHeadStats(player);
        inventory.setItem(0, playerHead);

        var animationsHead = createAnimationItem();
        var sniffer = createPetItem();

        inventory.setItem(12, animationsHead);
        inventory.setItem(14, sniffer);
        inventory.setItem(26, getSettingsHead());

        player.getBukkitPlayer().openInventory(inventory);
    }

    public static void displaySettingsInventory(Player player) {
        if (player.getBukkitPlayer() == null) return;

        Inventory inventory = Bukkit.createInventory(null, 27, "Settings");
        for (int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var item = getActivatedItem();
        if (!player.isShowScoreboard()) {
            item = getDeactivatedItem();
        }
        inventory.setItem(13, getInfosHead());
        inventory.setItem(14, item);
        inventory.setItem(18, backItem());

        player.getBukkitPlayer().openInventory(inventory);
    }

    private static ItemStack getDeactivatedItem() {
        var item = new ItemStack(Material.BARRIER);
        var meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Deactivated");
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getActivatedItem() {
        var item = new ItemStack(Material.SLIME_BALL);
        var meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Activated");
        item.setItemMeta(meta);

        return item;
    }
    private static ItemStack getInfosHead() {
        var head = Head.getCustomHead(Head.info);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Show Scoreboard");
        head.setItemMeta(meta);
        return head;
    }

    private static ItemStack getSettingsHead() {
        var head = Head.getCustomHead(Head.settings);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "Settings");
        head.setItemMeta(meta);
        return head;
    }
    private static ItemStack getPlayerHeadStats(Player player) {
        var playerHead = Head.getPlayerHead(player.getBukkitPlayer());
        var playerMeta = playerHead.getItemMeta();
        playerMeta.setDisplayName(ChatColor.GOLD + player.getPlayerName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Played time: " + ChatColor.WHITE + LeaderBoard.formatPlayTime(player.getPlayedTime()));
        lore.add(ChatColor.GREEN + "Plots: " + ChatColor.WHITE + player.getPlots().size());
        lore.add(ChatColor.GREEN + "Total blocks claim: " + ChatColor.WHITE + player.getTotalBlocks());
        lore.add(ChatColor.GREEN + "BsBucks: " + ChatColor.WHITE + player.getBsBucks());
        lore.add(ChatColor.GREEN + "Total BsBucks: " + ChatColor.WHITE + player.getTotalEstimatedFortune());
        lore.add(ChatColor.RED + "Deaths: " + ChatColor.WHITE + player.getDeaths());
        playerMeta.setLore(lore);
        playerHead.setItemMeta(playerMeta);

        return playerHead;
    }
}
