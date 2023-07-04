package fr.louisbillaut.bettersurvival.listeners;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Player;
import fr.louisbillaut.bettersurvival.game.Plot;
import fr.louisbillaut.bettersurvival.utils.Selector;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {
    private Game game;
    private Main instance;
    public PlayerListener(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();

        var armorStands = game.armorStands;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (armorStands.containsKey(player.getUniqueId())) {
                var as = armorStands.get(player.getUniqueId());
                ArmorStand armorStand = as.get(as.size() -1);
                player.sendMessage(ChatColor.YELLOW + "Coords : X:" +
                        armorStand.getLocation().getBlockX() + " Y:" +
                        armorStand.getLocation().getBlockY() + " Z:" +
                        armorStand.getLocation().getBlockZ());
                if (player.hasMetadata("createPos1")) {
                    player.removeMetadata("createPos1", instance);
                    player.setMetadata("createPos2", new FixedMetadataValue(instance, true));
                    Selector.appearArmorStand(instance, game, player);
                    return;
                }
                if (player.hasMetadata("createPos2")) {
                    player.removeMetadata("createPos2", instance);
                    player.removeMetadata("createPos1", instance);
                    if (as.size() >= 2) {
                        String name = "";
                        if (player.hasMetadata("plotName")) {
                            for(MetadataValue md : player.getMetadata("plotName")) {
                                if (!md.asString().equals("")) {
                                    name = md.asString();
                                }
                            }
                            if (!name.equals("")) {
                                if (player.hasMetadata("plotHeight")) {
                                    for(MetadataValue md : player.getMetadata("plotHeight")) {
                                        if (md.asInt() != 0) {
                                            player.removeMetadata("plotName", instance);
                                            player.removeMetadata("plotHeight", instance);
                                            Location pos1 = as.get(as.size() -2).getLocation();
                                            Location pos2 = as.get(as.size() -1).getLocation();
                                            Player playerIG = game.getPlayer(player);
                                            playerIG.addPlot(new Plot(player, pos1, pos2, md.asInt(), name));
                                            player.spigot().sendMessage();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    removeAllArmorStandsAndTasks(player);
                }
            }
        }
    }

    private void sendActionBar(org.bukkit.entity.Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private void showNumberOfBlock(org.bukkit.entity.Player player) {
        var armorStands = game.armorStands;
        var as = armorStands.get(player.getUniqueId());
        if (as.size() < 2) {
            return;
        }
        Location pos1 = as.get(as.size() -2).getLocation();
        Location pos2 = as.get(as.size() -1).getLocation();
        if (player.hasMetadata("plotHeight")) {
            for (MetadataValue md : player.getMetadata("plotHeight")) {
                if (md.asInt() != 0) {
                    int numberOfBlocks = computeNumberOfBlocks(pos1, pos2, md.asInt());
                    sendActionBar(player, ChatColor.YELLOW + "Number of Blocks: " + ChatColor.BOLD + numberOfBlocks);
                }
            }
        }
    }

    private int computeNumberOfBlocks(Location coin1, Location coin2, int height) {
        int minX = Math.min(coin1.getBlockX(), coin2.getBlockX());
        int minY = Math.min(coin1.getBlockY(), coin2.getBlockY());
        int minZ = Math.min(coin1.getBlockZ(), coin2.getBlockZ());

        int maxX = Math.max(coin1.getBlockX(), coin2.getBlockX());
        int maxY = Math.max(coin1.getBlockY(), coin2.getBlockY());
        int maxZ = Math.max(coin1.getBlockZ(), coin2.getBlockZ());

        int numBlocks = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (y >= minY && y <= minY + height - 1) {
                        numBlocks++;
                    }
                }
            }
        }

        return numBlocks;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        var armorStands = game.armorStands;
        if (armorStands.containsKey(player.getUniqueId())) {
            var as = armorStands.get(player.getUniqueId());
            ArmorStand armorStand = as.get(as.size()-1);
            Block targetBlock = player.getTargetBlock(null, 100);
            if (targetBlock.getType() != Material.AIR) {
                armorStand.teleport(targetBlock.getLocation().add(0.5, 0.6, 0.5));
                showNumberOfBlock(player);
            }
        }
    }

    private void removeAllArmorStandsAndTasks(org.bukkit.entity.Player player) {
        var armorStands = game.armorStands;
        if (armorStands.containsKey(player.getUniqueId())) {
            var as = armorStands.get(player.getUniqueId());
            as.forEach(Entity::remove);
            armorStands.remove(player.getUniqueId());
        }
        var rotationTasks = game.armorStandsRotationTasks;
        if (rotationTasks.containsKey(player.getUniqueId())) {
            var rt = rotationTasks.get(player.getUniqueId());
            rt.forEach(BukkitRunnable::cancel);
            rotationTasks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        removeAllArmorStandsAndTasks(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = game.getPlayer(event.getPlayer());
        if (player == null) {
            Bukkit.getLogger().info("player not found, creating new player ...");
            player = new Player(event.getPlayer().getDisplayName());
            game.addPlayer(player);
        } else {
            Bukkit.getLogger().info("player found.");
            player.setBukkitPlayer(event.getPlayer());
        }
    }

    private void plotSettingClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Plot Settings")) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                Player player = game.getPlayer((org.bukkit.entity.Player) event.getWhoClicked());
                if(player.getBukkitPlayer().hasMetadata("setting")) {
                    for(MetadataValue mv: player.getBukkitPlayer().getMetadata("setting")) {
                        Plot plot = player.getPlot(mv.asString());
                        if (plot != null) {
                            plot.toggleInteractStatus(instance, player.getBukkitPlayer(), event.getInventory(), clickedItem, event.getSlot());
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void plotHeightClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Plots Options")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.GRASS_BLOCK) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                String sizeString = ChatColor.stripColor(displayName).replace("Height ", "");
                int size = -1;
                if (!sizeString.equals("infinite")){
                    size = Integer.parseInt(sizeString);
                }
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.setMetadata("plotHeight", new FixedMetadataValue(instance, size));
                player.setMetadata("createPos1", new FixedMetadataValue(instance, true));
                player.closeInventory();
                Selector.appearArmorStand(instance, game, player);
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plotSettingClickEvent(event);
        plotHeightClickEvent(event);
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        List<String> bookContent = event.getNewBookMeta().getPages();
        ArrayList<String> whiteList = new ArrayList<>();

        for (String page : bookContent) {
            String[] lines = page.split("\n");
            for (String line : lines) {
                whiteList.add(line);
            }
        }

        Player player = game.getPlayer(event.getPlayer());
        if(player.getBukkitPlayer().hasMetadata("setting")) {
            for(MetadataValue mv: player.getBukkitPlayer().getMetadata("setting")) {
                Plot plot = player.getPlot(mv.asString());
                if (plot == null) return;
                if(player.getBukkitPlayer().hasMetadata("whitelist")){
                    for(MetadataValue m: player.getBukkitPlayer().getMetadata("whitelist")) {
                        plot.setWhitelist(whiteList, m.asString());
                        return;
                    }
                }
            }
        }
    }
}
