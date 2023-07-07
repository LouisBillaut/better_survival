package fr.louisbillaut.bettersurvival.listeners;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Player;
import fr.louisbillaut.bettersurvival.game.Plot;
import fr.louisbillaut.bettersurvival.utils.Detector;
import fr.louisbillaut.bettersurvival.utils.Selector;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayerListener implements Listener {
    private Game game;
    private Main instance;
    public PlayerListener(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    private void armorStandSelector(PlayerInteractEvent event) {
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

    private final List<Material> openableItems = Arrays.asList(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.ENCHANTING_TABLE,
            Material.BARREL,
            Material.DROPPER,
            Material.HOPPER,
            Material.BREWING_STAND,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.BARREL
    );

    private final List<Material> redstoneActivatableBlocks = Arrays.asList(
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.DAYLIGHT_DETECTOR,
            Material.TRIPWIRE,
            Material.TRIPWIRE_HOOK,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.COMPARATOR,
            Material.REDSTONE_WIRE,
            Material.REPEATER,
            Material.OBSERVER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.IRON_TRAPDOOR,
            Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.END_GATEWAY,
            Material.IRON_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.TRAPPED_CHEST
    );

    private void interactWhitelistDetector(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;
        var playersInGame = game.getPlayers();
        for(Player playerIG: playersInGame) {
            if(event.getPlayer().getDisplayName().equals(playerIG.getPlayerName())) continue;
            var plots = playerIG.getPlots();
            for(Plot p: plots) {
                if(p.getPlayerInteract().equals(Plot.PlotSetting.ACTIVATED)) {
                    continue;
                }

                Location blockClickedLocation = event.getClickedBlock().getLocation();
                if(Detector.isInZone(blockClickedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerInteract().equals(Plot.PlotSetting.DEACTIVATED)) {
                    if (openableItems.contains(event.getClickedBlock().getType()) || redstoneActivatableBlocks.contains(event.getClickedBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if(Detector.isInZone(blockClickedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerInteract().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerInteractWhitelist())) {
                    if (openableItems.contains(event.getClickedBlock().getType()) || redstoneActivatableBlocks.contains(event.getClickedBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private void buildPlaceWhitelistDetector(BlockPlaceEvent event) {
        var playersInGame = game.getPlayers();
        for(Player playerIG: playersInGame) {
            if(event.getPlayer().getDisplayName().equals(playerIG.getPlayerName())) continue;
            var plots = playerIG.getPlots();
            for(Plot p: plots) {
                if(p.getPlayerBuild().equals(Plot.PlotSetting.ACTIVATED)) {
                    continue;
                }

                Location blockPlacedLocation = event.getBlockPlaced().getLocation();
                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerBuild().equals(Plot.PlotSetting.DEACTIVATED)) {
                    event.setCancelled(true);
                    return;
                }
                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerBuild().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerBuildWhitelist())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void buildBreakWhitelistDetector(BlockBreakEvent event) {
        var playersInGame = game.getPlayers();
        for(Player playerIG: playersInGame) {
            if(event.getPlayer().getDisplayName().equals(playerIG.getPlayerName())) continue;
            var plots = playerIG.getPlots();
            for(Plot p: plots) {
                Location blockPlacedLocation = event.getBlock().getLocation();
                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerInteract().equals(Plot.PlotSetting.DEACTIVATED)) {
                    if (openableItems.contains(event.getBlock().getType()) || redstoneActivatableBlocks.contains(event.getBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerInteract().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerInteractWhitelist())) {
                    if (openableItems.contains(event.getBlock().getType()) || redstoneActivatableBlocks.contains(event.getBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if(p.getPlayerBuild().equals(Plot.PlotSetting.ACTIVATED)) {
                    continue;
                }

                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerBuild().equals(Plot.PlotSetting.DEACTIVATED)) {
                    event.setCancelled(true);
                    return;
                }
                if(Detector.isInZone(blockPlacedLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerBuild().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerBuildWhitelist())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private String getRandomMessage() {
        List<String> messages = List.of(
                Main.sendLocalizedMessage("privateAreaErr"),
                Main.sendLocalizedMessage("privateAreaErr2"),
                Main.sendLocalizedMessage("privateAreaErr3"),
                Main.sendLocalizedMessage("privateAreaErr4"),
                Main.sendLocalizedMessage("privateAreaErr5")
        );

        Random random = new Random();
        int index = random.nextInt(messages.size());
        return messages.get(index);
    }

    private void sendWelcomeTitle(org.bukkit.entity.Player player, String playerName, String plotName) {
        String title = ChatColor.GREEN + Main.sendLocalizedMessage("welcome");
        String subtitle = plotName + " " + Main.sendLocalizedMessage("of") + " " + playerName;
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        player.sendTitle(title, subtitle, 10, 70, 20);
    }
    private void enterWhitelistDetector(PlayerMoveEvent event) {
        var playersInGame = game.getPlayers();
        for(Player playerIG: playersInGame) {
            if(event.getPlayer().getDisplayName().equals(playerIG.getPlayerName())) continue;
            var plots = playerIG.getPlots();
            for(Plot p: plots) {
                Location playerLocation = event.getPlayer().getLocation();
                if(p.getPlayerEnter().equals(Plot.PlotSetting.ACTIVATED)) {
                    if(Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight()) && !event.getPlayer().hasMetadata(p.getName())) {
                        event.getPlayer().setMetadata(p.getName(), new FixedMetadataValue(instance, true));
                        sendWelcomeTitle(event.getPlayer(), playerIG.getPlayerName(), p.getName());
                    } else if (!Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight()) && event.getPlayer().hasMetadata(p.getName())){
                        if(event.getPlayer().hasMetadata(p.getName())) {
                            event.getPlayer().removeMetadata(p.getName(), instance);
                        }
                    }
                    continue;
                }
                Entity vehicle = event.getPlayer().getVehicle();
                if (vehicle != null) {
                    playerLocation = vehicle.getLocation();
                }
                Vector direction = playerLocation.getDirection().multiply(-0.5);
                if(Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.DEACTIVATED)) {
                    if(vehicle != null) {
                        if (vehicle.getType().equals(EntityType.MINECART)) {
                            vehicle.eject();
                        }
                        vehicle.setVelocity(direction);
                    } else {
                        event.getPlayer().setVelocity(direction);
                    }
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.getPlayer().sendMessage(ChatColor.RED + getRandomMessage());
                    return;
                }
                if(Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())) {
                    if(vehicle != null) {
                        if (vehicle.getType().equals(EntityType.MINECART)) {
                            vehicle.eject();
                        }
                        vehicle.setVelocity(direction);
                    } else {
                        event.getPlayer().setVelocity(direction);
                    }
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.getPlayer().sendMessage(ChatColor.RED + getRandomMessage());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        buildPlaceWhitelistDetector(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        buildBreakWhitelistDetector(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        armorStandSelector(event);
        interactWhitelistDetector(event);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if(!event.getVehicle().getType().equals(EntityType.MINECART)) {
            return;
        }
        for(Entity e : event.getVehicle().getPassengers()) {
            if (!e.getType().equals(EntityType.PLAYER)) {
                continue;
            }
            org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) e;
            var playersInGame = game.getPlayers();
            for(Player playerIG: playersInGame) {
                if(bukkitPlayer.getDisplayName().equals(playerIG.getPlayerName())) continue;
                var plots = playerIG.getPlots();
                for(Plot p: plots) {
                    if(p.getPlayerEnter().equals(Plot.PlotSetting.ACTIVATED)) {
                        continue;
                    }
                    if(Detector.isInZone(event.getVehicle().getLocation(), p.getLocation1(), p.getLocation2(), p.getHeight())
                            && p.getPlayerEnter().equals(Plot.PlotSetting.DEACTIVATED)) {
                        event.getVehicle().eject();
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        bukkitPlayer.sendMessage(ChatColor.RED + getRandomMessage());
                    }
                    if(Detector.isInZone(bukkitPlayer.getLocation(), p.getLocation1(), p.getLocation2(), p.getHeight())
                            && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                            && !Detector.isInWhiteList(bukkitPlayer, p.getPlayerEnterWhitelist())) {
                        event.getVehicle().eject();
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        bukkitPlayer.sendMessage(ChatColor.RED + getRandomMessage());
                    }
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

        enterWhitelistDetector(event);
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
        if (event.getView().getTitle().contains("plot settings")) {
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

    private void plotListClickEvent(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getView().getTitle().equals("Plots List")) {
            event.setCancelled(true);

            if (clickedItem.getType() == Material.GRASS_BLOCK) {
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta != null && itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (displayName != null && displayName.startsWith(ChatColor.GREEN + "Plot ")) {
                        String plotName = ChatColor.stripColor(displayName).replace("Plot ", "");
                        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
                        Plot plot = game.getPlayer(player).getPlot(plotName);
                        if (plot == null) {
                            player.sendMessage("Your don't have a plot named " + plotName);
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                        plot.openSettingsInventory(instance, player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plotSettingClickEvent(event);
        plotHeightClickEvent(event);
        plotListClickEvent(event);
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
