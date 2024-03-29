package fr.louisbillaut.bettersurvival.listeners;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.animations.*;
import fr.louisbillaut.bettersurvival.game.*;
import fr.louisbillaut.bettersurvival.game.Player;
import fr.louisbillaut.bettersurvival.pets.*;
import fr.louisbillaut.bettersurvival.utils.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.*;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.louisbillaut.bettersurvival.game.Cosmetics.renamePetPrice;

public class PlayerListener implements Listener {
    private Game game;
    private Main instance;
    private static int emptyTradeSlot = 12;
    private static int amountSlot = 11;
    private Map<org.bukkit.entity.Player, BukkitRunnable> animationsTasks = new HashMap<>();
    private Map<org.bukkit.entity.Player, BukkitRunnable> rotationTasks = new HashMap<>();
    private Map<org.bukkit.entity.Player, ArmorStand> armorStands = new HashMap<>();
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
                                            if(playerIG == null) return;
                                            var numberOfBlocks = computeNumberOfBlocks(pos1, pos2, md.asInt());
                                            double price = computePrice(numberOfBlocks, playerIG);
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
                    boolean sendWebhooks = false;
                    if(shop.getVillager() == null) {
                        sendWebhooks = true;
                    }
                    Location loc = armorStand.getLocation();
                    loc.setY(Math.floor(armorStand.getLocation().getY() + 1));
                    shop.createCustomVillager(instance, loc);
                    removeAllArmorStandsAndTasks(player);
                    if(!sendWebhooks) return;
                    for(Trade t: shop.getTradeList()) {
                        instance.sendWebhookMessage(player.getDisplayName(), shop, t);
                    }
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
                        playerIG.sendWelcomeTitle(event.getPlayer(), playerIG.getPlayerName(), p.getName());
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
                if(Detector.isInZone(event.getTo(), p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.DEACTIVATED)) {
                    if(vehicle != null) {
                        if (vehicle.getType().equals(EntityType.MINECART)) {
                            vehicle.eject();
                        }
                        vehicle.setVelocity(direction);
                    } else {
                        event.getPlayer().teleport(event.getFrom());
                    }
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.getPlayer().sendMessage(ChatColor.RED + getRandomMessage());
                    return;
                }
                if(Detector.isInZone(event.getTo(), p.getLocation1(), p.getLocation2(), p.getHeight())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && !Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())) {
                    if(vehicle != null) {
                        if (vehicle.getType().equals(EntityType.MINECART)) {
                            vehicle.eject();
                        }
                        vehicle.setVelocity(direction);
                    } else {
                        event.getPlayer().teleport(event.getFrom());
                    }
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.getPlayer().sendMessage(ChatColor.RED + getRandomMessage());
                    return;
                }
                if (Detector.isInZone(event.getTo(), p.getLocation1(), p.getLocation2(), p.getHeight())
                        && !event.getPlayer().hasMetadata(p.getName())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())) {
                    event.getPlayer().setMetadata(p.getName(), new FixedMetadataValue(instance, true));
                    playerIG.sendWelcomeTitle(event.getPlayer(), playerIG.getPlayerName(), p.getName());
                }
                if (!Detector.isInZone(event.getTo(), p.getLocation1(), p.getLocation2(), p.getHeight())
                        && event.getPlayer().hasMetadata(p.getName())
                        && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                        && Detector.isInWhiteList(event.getPlayer(), p.getPlayerEnterWhitelist())){
                    event.getPlayer().removeMetadata(p.getName(), instance);
                }
            }
        }
    }

    private void silkTouchEvent(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (hasSilkTouchEnchantment(tool) && event.getBlock().getType() == Material.SPAWNER) {
            event.setCancelled(true);

            Block spawnerBlock = event.getBlock();
            ItemStack spawnerItem = new ItemStack(Material.SPAWNER);

            BlockStateMeta meta = (BlockStateMeta) spawnerItem.getItemMeta();
            CreatureSpawner capturedSpawner = (CreatureSpawner) spawnerBlock.getState();
            CreatureSpawner newSpawner = (CreatureSpawner) meta.getBlockState();
            newSpawner.setSpawnedType(capturedSpawner.getSpawnedType());
            meta.setBlockState(newSpawner);

            meta.getPersistentDataContainer().set(new NamespacedKey(instance, "mobtype"), PersistentDataType.STRING, capturedSpawner.getSpawnedType().toString());

            String displayName = capturedSpawner.getSpawnedType().toString().toLowerCase();
            if (displayName != null) {
                meta.setDisplayName(ChatColor.GOLD + displayName + " spawner");
            }

            spawnerItem.setItemMeta(meta);

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), spawnerItem);

            spawnerBlock.setType(Material.AIR);
        }
    }

    private void getLavaGhostBook(org.bukkit.entity.Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Lava Ghost");
        bookMeta.setAuthor("Ancester");
        bookMeta.addPage("Dans les terres ancestrales, enfouies sous les voiles du temps, existe un pouvoir oublié. \n Le corps du Lava Ghost, caché au creux des entrailles de la terre, attend celui qui osera révéler son essence.\n");
        bookMeta.addPage("Arme ta Pioche des Souvenirs, frappe la pierre et libère le corps du Lava Ghost.");
        bookMeta.addPage("Assemble le Lava Ghost et libère son pouvoir !");
        book.setItemMeta(bookMeta);

        player.openBook(book);
    }

    private void lavaGhostHeadEvent(BlockBreakEvent event) {
        if (event.getBlock() == null || event.getBlock().getState() == null) return;
        TileState tileState;
        try {
            tileState = (TileState) event.getBlock().getState();
        } catch (ClassCastException e) {
            return;
        }
        PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();
        if(dataContainer.has(new NamespacedKey(instance, "easter"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            Player playerIG = game.getPlayer(event.getPlayer());
            if (playerIG == null) return;
            if (playerIG.getCosmetics().hasPet(new LavaGhost())){
                return;
            }
            giveOrDropItem(event.getPlayer(), EasterEgg.getLavaGhostHeadItem());
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 1.0f, 1.0f);
            getLavaGhostBook(event.getPlayer());
        }
    }

    private void lavaGhostBodyEvent(BlockBreakEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        Block brokenBlock = event.getBlock();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType().name().contains("PICKAXE") && heldItem.hasItemMeta() &&
                heldItem.getItemMeta().hasDisplayName() && heldItem.getItemMeta().getDisplayName().equals("Pioche des Souvenirs") &&
                brokenBlock.getType() == Material.STONE) {
            Player playerIG = game.getPlayer(event.getPlayer());
            if (playerIG == null) return;
            if (playerIG.getCosmetics().hasPet(new LavaGhost())) return;
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 1.0f, 1.0f);
            ItemStack magmaBlock = new ItemStack(Material.MAGMA_BLOCK);
            ItemMeta magmaMeta = magmaBlock.getItemMeta();
            magmaMeta.setDisplayName(ChatColor.RED + "Lava Ghost Body");
            magmaBlock.setItemMeta(magmaMeta);

            giveOrDropItem(player, magmaBlock);
        }
    }

    private void spawnerPlace(BlockPlaceEvent event) {
        ItemStack spawnerItem = event.getItemInHand();
        if (spawnerItem.getType() == Material.SPAWNER) {
            Block placedBlock = event.getBlockPlaced();
            BlockStateMeta meta = (BlockStateMeta) spawnerItem.getItemMeta();
            CreatureSpawner newSpawner = (CreatureSpawner) meta.getBlockState();

            if (meta.getPersistentDataContainer().has(new NamespacedKey(instance, "mobtype"), PersistentDataType.STRING)) {
                String mobType = meta.getPersistentDataContainer().get(new NamespacedKey(instance, "mobtype"), PersistentDataType.STRING);

                try {
                    org.bukkit.entity.EntityType entityType = org.bukkit.entity.EntityType.valueOf(mobType);
                    newSpawner.setSpawnedType(entityType);
                    placedBlock.setType(Material.SPAWNER);
                    CreatureSpawner placedSpawner = (CreatureSpawner) placedBlock.getState();
                    placedSpawner.setSpawnedType(newSpawner.getSpawnedType());
                    placedSpawner.update();
                    spawnerItem.setAmount(spawnerItem.getAmount() - 1);
                } catch (IllegalArgumentException ex) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void placeLavaGhost(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "Easter")) {
            TileState tileState = (TileState) event.getBlockPlaced().getState();
            PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();
            dataContainer.set(new NamespacedKey(instance, "easter"), PersistentDataType.STRING, "easter");
            tileState.update();
        }
    }

    private ArmorStand spawnArmorStand(Location location) {
        Location spawnLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setBasePlate(false);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(false);

        ItemStack redLeatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) redLeatherChestplate.getItemMeta();
        chestplateMeta.setColor(Color.RED);
        redLeatherChestplate.setItemMeta(chestplateMeta);
        armorStand.setChestplate(redLeatherChestplate);

        var head = Head.getCustomHead(Head.ghostLava);

        armorStand.setHelmet(head);

        return armorStand;
    }

    public void spawnFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);

        firework.detonate();
    }
    public void startAnimation(Player player, Location location) {
        ArmorStand armorStand = spawnArmorStand(location);
        armorStands.put(player.getBukkitPlayer(), armorStand);

        var animationTask = new BukkitRunnable() {
            private final double duration = 5.0;
            private final double yOffset = 3.0;

            private final Location initialLocation = armorStand.getLocation().clone();

            private final long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (armorStand.isValid() && !armorStand.isDead()) {
                    double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                    if (elapsed <= duration) {
                        double progress = elapsed / duration;
                        Location currentLocation = initialLocation.clone().add(0, yOffset * progress, 0);
                        armorStand.teleport(currentLocation);
                    } else {
                        armorStands.get(player.getBukkitPlayer()).remove();
                        animationsTasks.get(player.getBukkitPlayer()).cancel();
                        rotationTasks.get(player.getBukkitPlayer()).cancel();
                        player.getBukkitPlayer().playSound(player.getBukkitPlayer(), Sound.ENTITY_PARROT_IMITATE_WITHER, 1.0f, 1.0f);
                        spawnFirework(initialLocation.clone().add(0, yOffset, 0));
                    }
                }else {
                    animationsTasks.get(player.getBukkitPlayer()).cancel();
                    rotationTasks.get(player.getBukkitPlayer()).cancel();
                }
            }
        };

        var rotationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isValid() && !armorStand.isDead()) {
                    EulerAngle currentRotation = armorStand.getHeadPose();
                    EulerAngle newRotation = currentRotation.add(0, 0.15, 0);
                    armorStand.setHeadPose(newRotation);
                }else {
                    animationsTasks.get(player.getBukkitPlayer()).cancel();
                    rotationTasks.get(player.getBukkitPlayer()).cancel();
                }
            }
        };

        animationsTasks.put(player.getBukkitPlayer(), animationTask);
        rotationTasks.put(player.getBukkitPlayer(), rotationTask);
        animationTask.runTaskTimer(instance, 0, 1);
        rotationTask.runTaskTimerAsynchronously(instance, 0, 1);
    }

    private void placeLavaGhostHead(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Lava Ghost")) {
            TileState tileState = (TileState) event.getBlockPlaced().getState();
            PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();
            dataContainer.set(new NamespacedKey(instance, "lavaGhost"), PersistentDataType.STRING, "lavaGhost");
            tileState.update();
            Location blockLocation = event.getBlock().getLocation().clone().add(0, -1, 0);
            var block = Objects.requireNonNull(event.getBlock().getLocation().getWorld()).getBlockAt(blockLocation);
            if(block.hasMetadata("lavaGhostBody")) {
                block.setType(Material.AIR);
                event.getBlockPlaced().setType(Material.AIR);
                var playerIG = game.getPlayer(event.getPlayer());
                if (playerIG == null) return;
                playerIG.getBukkitPlayer().playSound(playerIG.getBukkitPlayer(), Sound.ENTITY_PARROT_IMITATE_WITHER, 1.0f, 1.0f);
                startAnimation(playerIG, blockLocation);
                playerIG.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Vous avez débloqué le familier " + ChatColor.RED + "Lava Ghost" + ChatColor.GREEN +" !");
                playerIG.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Vous pouvez l'équiper dans les pets de votre /profile");
                playerIG.getCosmetics().addPet(new LavaGhost());
            }
        }
    }

    private void placeLavaGhostBody(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Lava Ghost Body")) {
            event.getBlockPlaced().setMetadata("lavaGhostBody", new FixedMetadataValue(instance, true));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        buildPlaceWhitelistDetector(event);
        spawnerPlace(event);
        placeLavaGhost(event);
        placeLavaGhostBody(event);
        placeLavaGhostHead(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        buildBreakWhitelistDetector(event);
        silkTouchEvent(event);
        lavaGhostHeadEvent(event);
        lavaGhostBodyEvent(event);
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

    private double computePrice(int numberOfBlocks, Player player) {
        int nbOfBlocInPlots = 0;
        for (Plot p : player.getPlots()) {
            nbOfBlocInPlots += computeNumberOfBlocks(p.getLocation1(), p.getLocation2(), p.getHeight());
        }

        int nbOfPlotOfPlateau = (int) Math.floor(nbOfBlocInPlots / BsBucks.PlotNbOfBlocToIncreasePrice);
        double increasedPricePercent = BsBucks.blockPrice;
        for (var i = 0; i < nbOfPlotOfPlateau; i++) {
            increasedPricePercent += increasedPricePercent * BsBucks.PercentageAugmentation;
        }

        return Math.floor(numberOfBlocks * increasedPricePercent);
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
                    if(playerIG.getBsBucks() < computePrice(numberOfBlocks, playerIG)) {
                        color = ChatColor.RED;
                    }
                    ActionBar.sendActionBar(player, ChatColor.GREEN + "Price: " + color + computePrice(numberOfBlocks, playerIG) + " bsBucks");
                }
            }
        }
    }

    public static int computeNumberOfBlocks(Location coin1, Location coin2, int height) {
        int minX = Math.min(Math.abs(coin1.getBlockX()), Math.abs(coin2.getBlockX()));
        int minZ = Math.min(Math.abs(coin1.getBlockZ()), Math.abs(coin2.getBlockZ()));

        int maxX = Math.max(Math.abs(coin1.getBlockX()), Math.abs(coin2.getBlockX()));
        int maxZ = Math.max(Math.abs(coin1.getBlockZ()), Math.abs(coin2.getBlockZ()));

        int h = height;
        if (h == -1) {
            h = 300;
        }

        return (maxX - minX + 1) * (maxZ - minZ + 1) * h;
    }

    private void updateArmorStandPos(PlayerMoveEvent event) {
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

    private boolean hasPlayerMoved(Location from, Location to) {
        if (to == null) return false;
        return Math.floor(from.getX()) != Math.floor(to.getX()) || Math.floor(from.getY()) != Math.floor(to.getY()) || Math.floor(from.getZ()) != Math.floor(to.getZ());
    }

    private void checkSpawnTeleportCancel(PlayerMoveEvent event) {
        if (event.getPlayer().hasMetadata("teleportingSpawn")) {
            if (hasPlayerMoved(event.getFrom(), event.getTo())) {
                Player playerIG = game.getPlayer(event.getPlayer());
                if(playerIG == null) return;
                playerIG.getTeleportRunnable().cancel();
                event.getPlayer().removeMetadata("teleportingSpawn", instance);
                event.getPlayer().sendMessage(ChatColor.RED + "Teleportation to spawn canceled.");
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        updateArmorStandPos(event);
        enterWhitelistDetector(event);
        checkSpawnTeleportCancel(event);
    }

    private void checkCancelSpawnTeleport(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getEntity();
            if (player.hasMetadata("teleportingSpawn")) {
                Player playerIG = game.getPlayer(player);
                if(playerIG == null) return;
                playerIG.getTeleportRunnable().cancel();
                player.removeMetadata("teleportingSpawn", instance);
                player.sendMessage(ChatColor.RED + "Teleportation to spawn canceled.");
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
       checkCancelSpawnTeleport(event);
    }

    private void checkCancelSpawnTeleportDead(PlayerDeathEvent event) {
        org.bukkit.entity.Player player = event.getEntity();
        if (player.hasMetadata("teleportingSpawn")) {
            Player playerIG = game.getPlayer(player);
            if(playerIG == null) return;
            playerIG.getTeleportRunnable().cancel();
            player.removeMetadata("teleportingSpawn", instance);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        checkCancelSpawnTeleportDead(event);
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
        game.removePet(player);
        game.removeAnimation(player);
    }

    public void sendClickableMessage(org.bukkit.entity.Player player) {
        TextComponent messageComponent = new TextComponent(ChatColor.GREEN + "Don't forget to check commands: ");
        TextComponent linkComponent = new TextComponent(ChatColor.GOLD + "[Click Here]");
        linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bshelp"));
        messageComponent.addExtra(linkComponent);
        player.spigot().sendMessage(messageComponent);
        player.sendMessage(ChatColor.GREEN + "Check our tutos on our discord !");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = game.getPlayer(event.getPlayer());
        if (player == null) {
            player = new Player(instance, game, event.getPlayer().getDisplayName());
            game.addPlayer(player);
            displayTitle(event.getPlayer(), false);
            player = game.getPlayer(event.getPlayer());
            if (player != null) {
                player.login();
            }
            event.getPlayer().sendMessage(ChatColor.GREEN + "For your first connection we offer you: " + ChatColor.GOLD + "30000 bsBucks !");
        } else {
            Bukkit.getLogger().info("player found.");
            player.setBukkitPlayer(event.getPlayer());
            displayTitle(event.getPlayer(), true);
            player.login();
            if (player.getCosmetics().getActiveAnimation() != null) {
                player.getCosmetics().getActiveAnimation().startAnimation(instance, player.getBukkitPlayer());
                game.getAnimations().put(player.getBukkitPlayer(), player.getCosmetics().getActiveAnimation());
            }
            if (player.getCosmetics().getActivePet() != null) {
                player.getCosmetics().getActivePet().spawn(instance, player.getBukkitPlayer());
                game.getPets().put(player.getBukkitPlayer(), player.getCosmetics().getActivePet());
            }
        }
        sendClickableMessage(event.getPlayer());
        player.getCustomScoreboard().updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        var pet = game.getPets().get(event.getPlayer());
        if (pet != null) {
            pet.handleSneakToggle(instance, event.getPlayer(), event);
        }
        var animation = game.getAnimations().get(event.getPlayer());
        if (animation != null) {
            animation.handleSneakToggle(instance, event.getPlayer(), event);
        }
    }

    private void plotSettingClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("plot settings")) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                Player player = game.getPlayer((org.bukkit.entity.Player) event.getWhoClicked());
                if(event.getSlot() == 0) {
                    event.setCancelled(true);
                    var itemName = event.getClickedInventory().getItem(0).getItemMeta().getDisplayName();
                    var splited = itemName.split(" ");
                    if (splited.length < 2) return;
                    Plot plot = player.getPlot(splited[1]);
                    if (plot == null) return;
                    player.addTarget(instance, player.getBukkitPlayer(), plot.getLocation1(), plot.getName());
                    player.getBukkitPlayer().closeInventory();
                    return;
                }
                var clickedMeta = clickedItem.getItemMeta();
                if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                    player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                    Plot.displayListPlotInventory(instance, player);
                    return;
                }
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

            var clickedMeta = clickedItem.getItemMeta();
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
            var playerIG = game.getPlayer(player);
            if (playerIG == null) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "next")) {
                int page = 0;
                if(player.hasMetadata("plotListPage")) {
                    for (MetadataValue v: player.getMetadata("plotListPage")) {
                        page = v.asInt();
                    }
                }
                player.setMetadata("plotListPage", new FixedMetadataValue(instance, page + 1));
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayListPlotInventory(instance, playerIG);
            }

            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "previous")) {
                int page = 0;
                if(player.hasMetadata("plotListPage")) {
                    for (MetadataValue v: player.getMetadata("plotListPage")) {
                        page = v.asInt();
                    }
                }
                player.setMetadata("plotListPage", new FixedMetadataValue(instance, page - 1));
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayListPlotInventory(instance, playerIG);
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
                        Trade trade = new Trade(clickedInventory.getItem(emptyTradeSlot), itemsToExchange, shop.getActualNumberOfTrade());
                        shop.addTrade(trade);
                        shop.createShopInventory();
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
                        player.sendMessage(ChatColor.GREEN + "Trade added successfully !");
                        player.setMetadata("tradeSuccessful", new FixedMetadataValue(instance, true));
                        player.closeInventory();
                        instance.sendWebhookMessage(player.getDisplayName(), shop, trade);
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

    public int removeAllItemFromPlayerInventory(org.bukkit.entity.Player player, ItemStack itemStack) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int totalRemoved = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack currentItem = contents[i];
            if (currentItem != null && currentItem.isSimilar(itemStack)) {
                totalRemoved += currentItem.getAmount();
                inventory.setItem(i, null);
            }
        }

        return totalRemoved;
    }


    public static void giveOrDropItem(org.bukkit.entity.Player player, ItemStack item) {
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
        if(event.getSlot() == 7 ||
                event.getSlot() == 16 ||
                event.getSlot() == 25 ||
                event.getSlot() == 34 ||
                event.getSlot() == 43) {
            if(event.getClickedInventory().getItem(event.getSlot()) == null)return;
            Pattern pattern = Pattern.compile("([^:]*): (.*)");

            Matcher matcher = pattern.matcher(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName());
            if (!matcher.find() || matcher.groupCount() < 2) return;
            String playerName = ChatColor.stripColor(matcher.group(1));
            playerName = playerName.replaceAll("\\s", "");
            Player playerFromName = game.getPlayerByName(playerName);
            if (playerFromName == null ) return;
            String shopName = ChatColor.stripColor(matcher.group(2));
            shopName = shopName.replaceAll("\\s", "");
            Shop shop = playerFromName.getShop(shopName);
            if (shop == null) return;
            playerInGame.addTarget(instance, player, shop.getLocation(), shop.getName());
            player.closeInventory();
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
                var id = Integer.parseInt(stripped[1]) - 1;
                if (id >= shop.getTradeList().size()) return;
                Trade trade = shop.getTradeList().get(id);
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

        if(event.getSlot() == 0) {
            playerInGame.addTarget(instance, player, shop.getLocation(), shop.getName());
            player.closeInventory();
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
        if(!villager.isCustomNameVisible() && villager.getCustomName() == null) {
            if(game.getBs().containsItem(clickedItem)) {
                event.setCancelled(true);
                return;
            }
        }
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

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "sell")) {
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

        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "sell all")) {
            String strippedInput = ChatColor.stripColor(event.getClickedInventory().getItem(event.getSlot() - 3).getItemMeta().getDisplayName());
            int price = 0;
            try {
                price = Integer.parseInt(strippedInput);
            } catch (NumberFormatException e){
                return;
            }
            if(price == 0) return;
            ItemStack itemNeeded = event.getClickedInventory().getItem(event.getSlot() - 5);
            var nbSell = removeAllItemFromPlayerInventory(player, itemNeeded);
            if (nbSell == 0) {
                return;
            }
            playerInGame.addBsBucks(price * nbSell);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerInGame.getBsBucks() + " bsBucks");
        }
    }

    private void petShopClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Pets shop")) return;
        event.setCancelled(true);
        if(event.getClickedInventory().getItem(0) == null)return;
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerIG = game.getPlayer(player);
        if (playerIG == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("OWNED")) return;
        if (clickedMeta != null && (clickedMeta.getDisplayName().equals(" ") || clickedMeta.getDisplayName().equals(ChatColor.GOLD + "Pets"))) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Cosmetics.displayCosmeticsShop(playerIG);
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
        Pet.displayValidatePetBuy(player, clickedItem);
    }

    private void animationShopClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Animations shop")) return;
        event.setCancelled(true);
        if(event.getClickedInventory().getItem(0) == null)return;
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerIG = game.getPlayer(player);
        if (playerIG == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("OWNED")) return;
        if (clickedMeta != null && (clickedMeta.getDisplayName().equals(" ") || clickedMeta.getDisplayName().equals(ChatColor.GOLD + "Animations"))) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Cosmetics.displayCosmeticsShop(playerIG);
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
        Animation.displayValidateAnimationBuy(player, clickedItem);
    }

    private void animationValidateClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Animation Validate")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "buy")) {
            ItemStack itemStackPet = event.getClickedInventory().getItem(event.getSlot() - 2);
            Animation animation = Animation.getAnimationFromName(itemStackPet.getItemMeta().getDisplayName());
            if (animation == null) return;
            int price = animation.getPrice();
            if (playerInGame.getBsBucks() < animation.getPrice()) {
                player.sendMessage(ChatColor.RED + "You don't have enough bsBucks");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            playerInGame.setBsBucks(playerInGame.getBsBucks() - price);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "Animation " + animation.getItem().getItemMeta().getDisplayName() + ChatColor.GREEN + " purchased successfully !");
            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerInGame.getBsBucks() + " bsBucks");
            playerInGame.getCosmetics().addAnimation(animation);
            player.closeInventory();
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Animation.displayAllAnimationsInventory(playerInGame);
            return;
        }
    }

    private void petValidateClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Pet Validate")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "buy")) {
            ItemStack itemStackPet = event.getClickedInventory().getItem(event.getSlot() - 2);
            Pet pet = Pet.getPetFromName(itemStackPet.getItemMeta().getDisplayName());
            if (pet == null) return;
            int price = pet.getPrice();
            if (playerInGame.getBsBucks() < pet.getPrice()) {
                player.sendMessage(ChatColor.RED + "You don't have enough bsBucks");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            playerInGame.setBsBucks(playerInGame.getBsBucks() - price);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "Pet " + pet.getItem().getItemMeta().getDisplayName() + ChatColor.GREEN + " purchased successfully !");
            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerInGame.getBsBucks() + " bsBucks");
            playerInGame.getCosmetics().addPet(pet);
            player.closeInventory();
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Pet.displayAllPetsInventory(playerInGame);
            return;
        }
    }

    private void cosmeticsClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("bs shop")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Pets")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Pet.displayAllPetsInventory(playerInGame);
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Animations")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Animation.displayAllAnimationsInventory(playerInGame);
            return;
        }
    }

    private void profileClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Profile")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Pets")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.getCosmetics().displayOwnedPets(playerInGame);
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Animations")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.getCosmetics().displayOwnedAnimations(playerInGame);
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Settings")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Profile.displaySettingsInventory(playerInGame);
            return;
        }
    }

    private void profileSettingsClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (!event.getView().getTitle().contains("Settings")) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        ItemMeta clickedMeta = clickedItem.getItemMeta();
        Player playerInGame = game.getPlayer(player);
        if (playerInGame == null) return;
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("back")) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            Profile.displayProfileInventory(playerInGame);
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Deactivated")) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            playerInGame.setShowScoreboard(true);
            playerInGame.getCustomScoreboard().updateScoreboard(playerInGame);
            Profile.displaySettingsInventory(playerInGame);
            return;
        }
        if (clickedMeta != null && clickedMeta.getDisplayName().contains("Activated")) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            playerInGame.setShowScoreboard(false);
            playerInGame.getCustomScoreboard().setEmptyScoreboard(playerInGame);
            Profile.displaySettingsInventory(playerInGame);
            return;
        }
    }

    private void profileYourCosmeticsClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Your pets")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && (clickedMeta.getDisplayName().equals(" ") || clickedMeta.getDisplayName().equals(ChatColor.GOLD + "Pets"))) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Profile.displayProfileInventory(playerInGame);
                return;
            }
            if (clickedMeta != null && clickedMeta.getDisplayName().contains("remove")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "Pet removed.");
                game.removePet(player);
                playerInGame.getCosmetics().setActivePet(null);
                player.closeInventory();
                return;
            }
            if (clickedMeta != null && clickedMeta.getDisplayName().contains("rename")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                playerInGame.getCosmetics().displayOwnedRenamePets(playerInGame);
                return;
            }
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.getCosmetics().displayEquipPet(playerInGame, clickedItem);
        }
        if (event.getView().getTitle().contains("Your animations")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && (clickedMeta.getDisplayName().equals(" ") || clickedMeta.getDisplayName().equals(ChatColor.GOLD + "Animations"))) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Profile.displayProfileInventory(playerInGame);
                return;
            }
            if (clickedMeta != null && clickedMeta.getDisplayName().contains("remove")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "Animation removed.");
                game.removeAnimation(player);
                playerInGame.getCosmetics().setActiveAnimation(null);
                player.closeInventory();
                return;
            }
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            playerInGame.getCosmetics().displayEquipAnimation(playerInGame, clickedItem);
        }
    }

    private void profileEquipClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Equip pet")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                playerInGame.getCosmetics().displayOwnedPets(playerInGame);
                return;
            }
            if (event.getSlot() == 14) {
                game.removePet(player);
                playerInGame.getCosmetics().setActivePet(null);
                var petItem = event.getClickedInventory().getItem(12);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                var pet = Pet.getPetFromName(petItem.getItemMeta().getDisplayName());
                pet.spawn(instance, player);
                game.getPets().put(player, pet);
                playerInGame.getCosmetics().setActivePet(pet);
                player.closeInventory();
                player.sendMessage(petItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " equiped !");
            }
        }
        if (event.getView().getTitle().contains("Equip animation")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                playerInGame.getCosmetics().displayOwnedAnimations(playerInGame);
                return;
            }
            if (event.getSlot() == 14) {
                game.removeAnimation(player);
                playerInGame.getCosmetics().setActiveAnimation(null);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                var animationItem = event.getClickedInventory().getItem(12);
                var animation = Animation.getAnimationFromName(animationItem.getItemMeta().getDisplayName());
                animation.startAnimation(instance, player);
                game.getAnimations().put(player, animation);
                playerInGame.getCosmetics().setActiveAnimation(animation);
                player.closeInventory();
                player.sendMessage(animationItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " equiped !");
            }
        }
    }

    private void sendRenamePetCommand(org.bukkit.entity.Player player, String petName) {
        String command = "/rename " + petName + " ";
        String addPlayerMessage = ChatColor.GOLD + "[rename pet]";
        player.spigot().sendMessage(
                Messages.createClickableMessage(addPlayerMessage, command)
        );
    }

    private void profileRenamePetClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Rename your pets")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GRAY + "back")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                playerInGame.getCosmetics().displayOwnedPets(playerInGame);
                return;
            }
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(" ") || clickedMeta != null && clickedMeta.getDisplayName().contains("rename")) return;
            Pet pet = Pet.getPetFromName(event.getCurrentItem().getItemMeta().getDisplayName());
            if (pet == null) return;
            if((playerInGame.getBsBucks() - renamePetPrice) < 0) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "You don't have enough bsBucks");
                return;
            }
            sendRenamePetCommand(player, pet.getName());
            player.closeInventory();
        }
    }

    private void plotTypeClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Select plots")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "Whitelisted Plots")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayWhitelistedPlotInventory(instance, game, playerInGame);
                return;
            }
            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "My Plots")) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayListPlotInventory(instance, playerInGame);
                return;
            }
        }
    }

    private void plotWhitelistClickEvent(InventoryClickEvent event) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().equals(player.getInventory())) {
            return;
        }
        if (event.getView().getTitle().contains("Whitelisted Plots")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null) return;
            ItemMeta clickedMeta = clickedItem.getItemMeta();
            Player playerInGame = game.getPlayer(player);
            if (playerInGame == null) return;

            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "next")) {
                int page = 0;
                if(player.hasMetadata("plotWhiteListPage")) {
                    for (MetadataValue v: player.getMetadata("plotWhiteListPage")) {
                        page = v.asInt();
                    }
                }
                player.setMetadata("plotWhiteListPage", new FixedMetadataValue(instance, page + 1));
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayWhitelistedPlotInventory(instance, game, playerInGame);
            }

            if (clickedMeta != null && clickedMeta.getDisplayName().equals(ChatColor.GREEN + "previous")) {
                int page = 0;
                if(player.hasMetadata("plotWhiteListPage")) {
                    for (MetadataValue v: player.getMetadata("plotWhiteListPage")) {
                        page = v.asInt();
                    }
                }
                player.setMetadata("plotWhiteListPage", new FixedMetadataValue(instance, page - 1));
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
                Plot.displayWhitelistedPlotInventory(instance, game, playerInGame);
            }
            if (clickedMeta != null && !clickedMeta.getDisplayName().equals(" ")) {
                var itemName = event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName();
                var itemLore = event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getLore();
                var splited = itemName.split(" ");
                var playerName = "";
                if (splited.length < 2) return;
                if (itemLore != null && itemLore.size() > 2) {
                    var plotOf = itemLore.get(1);
                    var splitedOf = plotOf.split(" ");
                    if (splitedOf.length < 3) return;
                    playerName = ChatColor.stripColor(splitedOf[2]);
                }
                Player playerHasPlot = game.getPlayerByName(playerName);
                if (playerHasPlot == null) return;
                Plot plot = playerHasPlot.getPlot(splited[1]);
                if (plot == null) return;
                playerInGame.addTarget(instance, playerInGame.getBukkitPlayer(), plot.getLocation1(), plot.getName());
                playerInGame.getBukkitPlayer().closeInventory();
                return;
            }
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
        petShopClickEvent(event);
        petValidateClickEvent(event);
        animationShopClickEvent(event);
        animationValidateClickEvent(event);
        cosmeticsClickEvent(event);
        profileClickEvent(event);
        profileYourCosmeticsClickEvent(event);
        profileEquipClickEvent(event);
        profileRenamePetClickEvent(event);
        profileSettingsClickEvent(event);
        plotTypeClickEvent(event);
        plotWhitelistClickEvent(event);
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

    private boolean hasSilkTouchEnchantment(ItemStack item) {
        Enchantment silkTouch = new EnchantmentWrapper(Enchantment.SILK_TOUCH.getKey().getKey());
        return item.containsEnchantment(silkTouch);
    }

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        World world = player.getWorld();

        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int currentSleeping = game.getSleepingPlayersCount().getOrDefault(world, 0) + 1;
            game.putSleepingPlayer(world, currentSleeping);

            checkSleepPercentage(world);
        }
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        World world = player.getWorld();

        int currentSleeping = game.getSleepingPlayersCount().getOrDefault(world, 0) - 1;
        if (currentSleeping <= 0) {
            game.removeSleepingPlayer(world);
        } else {
            game.putSleepingPlayer(world, currentSleeping);
            checkSleepPercentage(world);
        }
    }

    private void checkSleepPercentage(World world) {
        int onlinePlayers = world.getPlayers().size();
        int currentSleeping = game.getSleepingPlayersCount().getOrDefault(world, 0);
        double percentageSleeping = (double) currentSleeping / onlinePlayers;
        if (percentageSleeping >= game.getSleepPercentageThreshold()) {
            world.setTime(0);
            game.removeSleepingPlayer(world);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE) && event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.isCustomNameVisible()) {
                event.setCancelled(true);
                event.getPlayer().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                event.getPlayer().sendMessage(ChatColor.RED + "You can't trade with a villager while in Hero of the village effect.");
            }
        }
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Bukkit.getLogger().info("event: " + event.getStatus());
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            String link = "http://resourcepack.host/dl/fOXGyo9v0Z9iIBGafsY80eN8ZivJ3Jh4/bs.zip";
            TextComponent message = new TextComponent(ChatColor.GREEN + "To download pack : ");
            TextComponent linkText = new TextComponent(ChatColor.YELLOW + "Click here");
            linkText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
            message.addExtra(linkText);
            event.getPlayer().sendMessage(ChatColor.RED + "You have declined server texture pack. It can affect your gaming experience.");
            event.getPlayer().spigot().sendMessage( message);
        }
    }
}
