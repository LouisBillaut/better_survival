package fr.louisbillaut.bettersurvival.listeners;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.*;
import fr.louisbillaut.bettersurvival.game.Player;
import fr.louisbillaut.bettersurvival.utils.Detector;
import fr.louisbillaut.bettersurvival.utils.Selector;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
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
    private static int emptyTradeSlot = 12;
    private static int amountSlot = 11;
    public PlayerListener(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    public void displayTitle(org.bukkit.entity.Player player, boolean isBack) {
        String title = ChatColor.GREEN + "BETTER SURVIVAL";
        String subtitle = ChatColor.GRAY + "Welcome back";
        if(!isBack) {
            subtitle = ChatColor.GRAY + "Welcome";
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        player.sendTitle(title, subtitle, 10, 70, 20);
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
                    if(game.isLocationInPlotWithRadius(player, armorStand.getLocation())) {
                        removeAllArmorStandsAndTasks(player);
                        return;
                    }
                    player.setMetadata("createPos2", new FixedMetadataValue(instance, true));
                    Selector.appearArmorStand(instance, game, player);
                    return;
                }
                if (player.hasMetadata("createPos2")) {
                    player.removeMetadata("createPos2", instance);
                    player.removeMetadata("createPos1", instance);
                    if(game.isLocationInPlotWithRadius(player, armorStand.getLocation())) {
                        removeAllArmorStandsAndTasks(player);
                        return;
                    }
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
                                            double price = Math.floor(computeNumberOfBlocks(pos1, pos2, md.asInt()) * BsBucks.blockPrice);
                                            if(playerIG.getBsBucks() < price) {
                                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                                                player.sendMessage(ChatColor.RED + "You don't have enough bsBucks");
                                                removeAllArmorStandsAndTasks(player);
                                                return;
                                            }
                                            playerIG.addPlot(new Plot(player, pos1, pos2, md.asInt(), name));
                                            player.spigot().sendMessage();
                                            playerIG.setBsBucks((int) (playerIG.getBsBucks() - price));
                                            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerIG.getBsBucks() + " bsBucks");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    removeAllArmorStandsAndTasks(player);
                }
                if(player.hasMetadata("placeShop")) {
                    String shopName = "";
                    for(MetadataValue md: player.getMetadata("placeShop")) {
                        shopName = md.asString();
                        break;
                    }
                    player.removeMetadata("placeShop", instance);
                    if(game.isLocationInOtherPlayerPlot(player, armorStand.getLocation())) {
                        player.sendMessage(ChatColor.RED + "You can't select in a plot that is not your");
                        removeAllArmorStandsAndTasks(player);
                        return;
                    }
                    if(shopName.equals("")) return;
                    Player playerInGame = game.getPlayer(player);
                    if(playerInGame == null) return;
                    Shop shop = playerInGame.getShop(shopName);
                    if(shop == null) return;
                    Location loc = armorStand.getLocation();
                    loc.setY(Math.floor(armorStand.getLocation().getY() + 1));
                    shop.createCustomVillager(loc);
                    removeAllArmorStandsAndTasks(player);
                }
                if(player.hasMetadata("claimShop")) {
                    removeAllArmorStandsAndTasks(player);
                    player.removeMetadata("claimShop", instance);
                    if(game.isLocationInOtherPlayerPlot(player, armorStand.getLocation())) {
                        player.sendMessage(ChatColor.RED + "You can't select in a plot that is not your");
                        return;
                    }
                    Player playerInGame = game.getPlayer(player);
                    if(playerInGame == null) return;
                    if(playerInGame.getClaims().size() == 0) {
                        player.sendMessage(ChatColor.RED + "you don't have any item to claim");
                        return;
                    }
                    Location loc = armorStand.getLocation();
                    loc.setY(Math.floor(armorStand.getLocation().getY() + 1));
                    playerInGame.createChestWithClaims(loc);
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
                if (Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && !event.getPlayer().hasMetadata(p.getName())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())) {
                    event.getPlayer().setMetadata(p.getName(), new FixedMetadataValue(instance, true));
                    sendWelcomeTitle(event.getPlayer(), playerIG.getPlayerName(), p.getName());
                }
                if (!Detector.isInZone(playerLocation, p.getLocation1(), p.getLocation2(), p.getHeight())
                        && event.getPlayer().hasMetadata(p.getName())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())){
                    event.getPlayer().removeMetadata(p.getName(), instance);
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
        Player playerIG = game.getPlayer(player);
        if(playerIG == null) return;
        ChatColor color = ChatColor.GOLD;
        if (player.hasMetadata("plotHeight")) {
            for (MetadataValue md : player.getMetadata("plotHeight")) {
                if (md.asInt() != 0) {
                    int numberOfBlocks = computeNumberOfBlocks(pos1, pos2, md.asInt());
                    if(playerIG.getBsBucks() < Math.floor(numberOfBlocks * BsBucks.blockPrice)) {
                        color = ChatColor.RED;
                    }
                    sendActionBar(player, ChatColor.GREEN + "Price: " + color + Math.floor(numberOfBlocks * BsBucks.blockPrice) + " bsBucks");
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
            displayTitle(event.getPlayer(), false);
            player = game.getPlayer(event.getPlayer());
            if (player != null) {
                player.login();
            }
        } else {
            Bukkit.getLogger().info("player found.");
            player.setBukkitPlayer(event.getPlayer());
            displayTitle(event.getPlayer(), true);
            player.login();
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

            if (clickedItem.getType() != null && clickedItem.getType() == Material.GRASS_BLOCK) {
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

    private String getShopName(String title) {
        String[] parts = title.split("\\s+");
        String name = "";
        if (parts.length > 2) {
            name = parts[2];
        }

        return name;
    }

    private void shopListClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Shops List"))  return;
        Player playerInGame = game.getPlayer(player);
        if(playerInGame == null) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        if(clickedItem.getItemMeta().getDisplayName().contains(String.valueOf(ChatColor.GREEN))) {
            String strippedInput = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Shop shop = playerInGame.getShop(strippedInput);
            if(shop == null) return;
            shop.displayTrades(instance, player);
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            return;
        }
    }

    private void itemInShopClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Shop configuration")) {
            String name = getShopName(event.getView().getTitle());
            if(event.getSlot() != emptyTradeSlot) event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                ItemMeta clickedMeta = clickedItem.getItemMeta();
                for(Player pIG: game.getPlayers()) {
                    if(!pIG.getPlayerName().equals(player.getDisplayName())) continue;
                    Shop shop = pIG.getShop(name);
                    if (shop == null) continue;
                    if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.RED + "remove")) {
                        shop.removeItem(player, event.getSlot());
                        return;
                    }
                    if (clickedMeta != null && clickedMeta.getDisplayName().contains("Item to exchange")) {
                        shop.sendAddItemCommand(player);
                        return;
                    }
                    if(clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.RED + "cancel")) {
                        player.closeInventory();
                        return;
                    }
                    if(clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "validate")) {
                        Inventory clickedInventory = event.getClickedInventory();
                        if(clickedInventory.getItem(emptyTradeSlot) == null || clickedInventory.getItem(14) == null || clickedInventory.getItem(15) == null) return;
                        if(clickedInventory.getItem(emptyTradeSlot).getItemMeta() == null
                                || (clickedInventory.getItem(14).getItemMeta().getDisplayName().equals("Item to exchange 1") && (clickedInventory.getItem(15).getItemMeta().getDisplayName().equals("Item to exchange 2")))
                        ) {
                            return;
                        }
                        List<ItemStack> itemsToExchange = new ArrayList<>();
                        if(!clickedInventory.getItem(14).getItemMeta().getDisplayName().equals("Item to exchange 1")) {
                            itemsToExchange.add(clickedInventory.getItem(14));
                        }
                        if(!clickedInventory.getItem(15).getItemMeta().getDisplayName().equals("Item to exchange 2")) {
                            itemsToExchange.add(clickedInventory.getItem(15));
                        }
                        shop.addTrade(new Trade(clickedInventory.getItem(emptyTradeSlot), itemsToExchange, shop.getActualNumberOfTrade()));
                        shop.createShopInventory();
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
                        player.sendMessage(ChatColor.GREEN + "Trade added successfully !");
                        player.setMetadata("tradeSuccessful", new FixedMetadataValue(instance, true));
                        player.closeInventory();
                    }
                    if(clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "+")) {
                        Inventory clickedInventory = event.getClickedInventory();
                        if (clickedInventory.getItem(emptyTradeSlot) == null || clickedInventory.getItem(emptyTradeSlot).getItemMeta() == null) return;
                        if (!removeItemFromPlayerInventory(player, clickedInventory.getItem(emptyTradeSlot))) {
                            return;
                        }
                        player.setMetadata("tradeIncrement", new FixedMetadataValue(instance, true));
                        shop.incrementActualNumberOfTrade();
                        shop.setItemToBuy(clickedInventory.getItem(emptyTradeSlot));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                        player.openInventory(shop.getActualInventory());
                    }
                    if(clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "-")) {
                        Inventory clickedInventory = event.getClickedInventory();
                        if (clickedInventory.getItem(emptyTradeSlot) == null || clickedInventory.getItem(emptyTradeSlot).getItemMeta() == null) return;
                        if (shop.getActualNumberOfTrade() == 1) return;
                        player.setMetadata("tradeIncrement", new FixedMetadataValue(instance, true));
                        shop.decrementActualNumberOfTrade();
                        giveOrDropItem(player, clickedInventory.getItem(emptyTradeSlot));
                        shop.setItemToBuy(clickedInventory.getItem(emptyTradeSlot));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                        player.openInventory(shop.getActualInventory());
                    }
                    if(event.getSlot() == emptyTradeSlot && event.getClickedInventory().getItem(emptyTradeSlot) != null && shop.getActualNumberOfTrade() > 1) {
                        ItemStack itemStack = event.getClickedInventory().getItem(emptyTradeSlot).clone();
                        itemStack.setAmount(itemStack.getAmount() * (shop.getActualNumberOfTrade() - 1));
                        shop.setActualNumberOfTrade(1);
                        giveOrDropItem(player, itemStack);
                        event.getClickedInventory().setItem(amountSlot, Shop.createGreenGlassBlock(1));
                    }
                }
            }
        }
    }
    public boolean removeItemFromPlayerInventory(org.bukkit.entity.Player player, ItemStack itemStack) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack currentItem = contents[i];
            if (currentItem != null && currentItem.isSimilar(itemStack)) {
                int amountToRemove = itemStack.getAmount();
                int currentAmount = currentItem.getAmount();

                if (currentAmount < amountToRemove) {
                    return false;
                } else {
                    currentItem.setAmount(currentAmount - amountToRemove);
                    inventory.setItem(i, currentItem);
                    return true;
                }
            }
        }

        return false;
    }


    private void giveOrDropItem(org.bukkit.entity.Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            Location location = player.getLocation();
            Item droppedItem = location.getWorld().dropItem(location, item);
            droppedItem.setPickupDelay(0);
        } else {
            player.getInventory().addItem(item);
        }
    }

    private void shopAllTradesListClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("all trades")) return;
        event.setCancelled(true);
        if(event.getClickedInventory().getItem(0) == null)return;
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "next")) {
            int page = 0;
            if(player.hasMetadata("allTradeListPage")) {
                for (MetadataValue v: player.getMetadata("allTradeListPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("allTradeListPage", new FixedMetadataValue(instance, page + 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.displayAllTrades(instance, game);
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "previous")) {
            int page = 0;
            if(player.hasMetadata("allTradeListPage")) {
                for (MetadataValue v: player.getMetadata("allTradeListPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("allTradeListPage", new FixedMetadataValue(instance, page - 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.displayAllTrades(instance, game);
        }
    }

    private void shopTradesListClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("trades list")) return;
        event.setCancelled(true);
        if(event.getClickedInventory().getItem(0) == null)return;
        String name = ChatColor.stripColor(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName());
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        Shop shop = playerInGame.getShop(name);
        if (shop == null) return;
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.RED + "remove")) {
            ItemStack itemTradeNumber = event.getClickedInventory().getItem(event.getSlot()-7);
            if (itemTradeNumber == null) return;
            String strippedInput = ChatColor.stripColor(itemTradeNumber.getItemMeta().getDisplayName());
            var stripped = strippedInput.split(" ");
            if(stripped.length >= 2) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Trade trade = shop.getTradeList().get(Integer.parseInt(stripped[1]) - 1);
                if (trade == null) return;
                ItemStack itemStack = trade.getItemsToBuy().clone();
                itemStack.setAmount(itemStack.getAmount() * trade.getMaxTrade());
                giveOrDropItem(player, itemStack);
                shop.removeTrade(Integer.parseInt(stripped[1]) - 1);
                int recipeId = getMerchantRecipeFromTrade(shop.getVillager(), trade);
                if (recipeId == -1) return;
                List<MerchantRecipe> merchantRecipes = new ArrayList<>(shop.getVillager().getRecipes());
                merchantRecipes.remove(recipeId);
                shop.getVillager().setRecipes(merchantRecipes);
                shop.displayTrades(instance, player);
            }
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "add trade")) {
            if(shop.getTradeList().size() == Shop.getMaxTradeLimit()) {
                player.sendMessage(ChatColor.RED + "Max trade limit reached: " + Shop.getMaxTradeLimit());
                player.closeInventory();
                return;
            }
            player.openInventory(shop.getActualInventory());
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "place")) {
            player.closeInventory();
            player.setMetadata("placeShop", new FixedMetadataValue(instance, shop.getName()));
            Selector.appearArmorStand(instance, game, player);
            return;
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "next")) {
            int page = 0;
            if(player.hasMetadata("tradeListPage")) {
                for (MetadataValue v: player.getMetadata("tradeListPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("tradeListPage", new FixedMetadataValue(instance, page + 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            shop.displayTrades(instance, player);
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "previous")) {
            int page = 0;
            if(player.hasMetadata("tradeListPage")) {
                for (MetadataValue v: player.getMetadata("tradeListPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("tradeListPage", new FixedMetadataValue(instance, page - 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            shop.displayTrades(instance, player);
        }
    }

    private Shop findShopByVillager(List<Shop> shops, Villager villager) {
        for (Shop shop : shops) {
            if (shop.getVillager() == villager) {
                return shop;
            }
        }
        return null;
    }

    private void itemBuyToVillager(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (!(clickedInventory != null && clickedInventory.getHolder() instanceof Villager)) return;
        Villager villager = (Villager) clickedInventory.getHolder();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        Shop shop = findShopByVillager(game.getAllShops(), villager);
        if(shop == null) return;
        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }
        if (event.getSlot() != 2) return;
        List<ItemStack> itemsToExchange = new ArrayList<>();
        if(clickedInventory.getItem(0) != null) itemsToExchange.add(clickedInventory.getItem(0));
        if(clickedInventory.getItem(1) != null) itemsToExchange.add(clickedInventory.getItem(1));
        Trade trade = shop.getTradeByItemToBuy(itemsToExchange, clickedItem);
        if(trade == null) return;
        trade.setMaxTrade(trade.getMaxTrade() - 1);
        Player playerHasShop = game.getPlayerFromShop(shop);
        if (playerHasShop != null) {
            playerHasShop.addClaimsToShop(trade);
        }
        // removing trade from villager
        if (trade.getMaxTrade() <= 0) {
            shop.getTradeList().remove(trade);
            int recipeId = getMerchantRecipeFromTrade(villager, trade);
            if (recipeId == -1) return;
            List<MerchantRecipe> merchantRecipes = new ArrayList<>(villager.getRecipes());
            merchantRecipes.remove(recipeId);
            villager.setRecipes(merchantRecipes);
            if(clickedInventory.getItem(0) != null && trade.getItemsToExchange().size() > 0 && trade.getItemsToExchange().get(0) != null) {
                clickedInventory.getItem(0).setAmount(clickedInventory.getItem(0).getAmount() - trade.getItemsToExchange().get(0).getAmount());
            }
            if(clickedInventory.getItem(1) != null && trade.getItemsToExchange().size() > 1 && trade.getItemsToExchange().get(1) != null) {
                clickedInventory.getItem(1).setAmount(clickedInventory.getItem(1).getAmount() - trade.getItemsToExchange().get(1).getAmount());
            }
        }
    }

    private boolean recipeContainsTrade(MerchantRecipe recipe, Trade trade) {
        for(ItemStack i: recipe.getIngredients()) {
            if(i.getType().equals(Material.AIR)) continue;
            if (!trade.getItemsToExchange().contains(i)) return false;
        }

        return true;
    }
    private int getMerchantRecipeFromTrade(Villager villager, Trade trade) {
        for(var i = 0; i < villager.getRecipes().size(); i++) {
            if(!recipeContainsTrade(villager.getRecipes().get(i), trade)) continue;
            if (villager.getRecipes().get(i).getResult().equals(trade.getItemsToBuy())) {
                return i;
            }
        }

        return -1;
    }

    private void listClaimsClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("claims list")) return;
        event.setCancelled(true);
    }

    public void bsItemBuyEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("bsBuck market")) return;
        event.setCancelled(true);
        if(event.getClickedInventory().getItem(0) == null)return;
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "next")) {
            int page = 0;
            if(player.hasMetadata("bsBucksPage")) {
                for (MetadataValue v: player.getMetadata("bsBucksPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("bsBucksPage", new FixedMetadataValue(instance, page + 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            game.displayBsBucksInventoryToPlayer(instance, player);
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "previous")) {
            int page = 0;
            if(player.hasMetadata("bsBucksPage")) {
                for (MetadataValue v: player.getMetadata("bsBucksPage")) {
                    page = v.asInt();
                }
            }
            player.setMetadata("bsBucksPage", new FixedMetadataValue(instance, page - 1));
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            game.displayBsBucksInventoryToPlayer(instance, player);
        }

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "buy")) {
            String strippedInput = ChatColor.stripColor(event.getClickedInventory().getItem(event.getSlot() - 2).getItemMeta().getDisplayName());
            int price = 0;
            try {
                price = Integer.parseInt(strippedInput);
            } catch (NumberFormatException e){
                return;
            }
            if(price == 0) return;
            ItemStack itemNeeded = event.getClickedInventory().getItem(event.getSlot() - 4);
            if (!removeItemFromPlayerInventory(player, itemNeeded)) {
                return;
            }
            playerInGame.addBsBucks(price);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerInGame.getBsBucks() + " bsBucks");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plotSettingClickEvent(event);
        plotHeightClickEvent(event);
        plotListClickEvent(event);
        itemInShopClickEvent(event);
        shopListClickEvent(event);
        shopTradesListClickEvent(event);
        itemBuyToVillager(event);
        shopAllTradesListClickEvent(event);
        listClaimsClickEvent(event);
        bsItemBuyEvent(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (event.getView().getTitle().contains("Shop configuration")) {
            if(player.hasMetadata("tradeIncrement")) {
                player.removeMetadata("tradeIncrement", instance);
                return;
            }
            if(player.hasMetadata("tradeSuccessful")) {
                player.removeMetadata("tradeSuccessful", instance);
                return;
            }
            ItemStack item = inventory.getItem(emptyTradeSlot);
            if (item != null) {
                String name = getShopName(event.getView().getTitle());
                for(Player pIG: game.getPlayers()) {
                    if(!pIG.getPlayerName().equals(player.getDisplayName())) continue;
                    Shop shop = pIG.getShop(name);
                    if (shop == null) continue;
                    Inventory inv = shop.getActualInventory();
                    inv.setItem(emptyTradeSlot, null);
                    shop.setActualInventory(inv);
                    item.setAmount(item.getAmount() * shop.getActualNumberOfTrade());
                    giveOrDropItem(player, item);
                    shop.setActualNumberOfTrade(1);
                }
            }
        }
    }
}
