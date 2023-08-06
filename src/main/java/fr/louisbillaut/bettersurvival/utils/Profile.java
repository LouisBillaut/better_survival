package fr.louisbillaut.bettersurvival.utils;

import fr.louisbillaut.bettersurvival.game.LeaderBoard;
import fr.louisbillaut.bettersurvival.game.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static fr.louisbillaut.bettersurvival.animations.Animation.createAnimationItem;
import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;
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

        player.getBukkitPlayer().openInventory(inventory);
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
