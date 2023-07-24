package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Plot {
    public enum PlotSetting {
        ACTIVATED("ACTIVATED"), DEACTIVATED("DEACTIVATED"), CUSTOM("CUSTOM");

        private String name;
        private PlotSetting(String name) {
            this.name = name ;
        }

        public String getName() {
            return  this.name ;
        }

        public ItemStack getItem() {
            ItemStack interactStatus;
            if(name.equals(ACTIVATED.getName())) {
                interactStatus = new ItemStack(Material.SLIME_BALL);
            }
            else if(name.equals(DEACTIVATED.getName())) {
                interactStatus = new ItemStack(Material.BARRIER);
            }
            else {
                interactStatus = new ItemStack(Material.EMERALD);
            }
            ItemMeta interactStatusMeta = interactStatus.getItemMeta();
            interactStatusMeta.setDisplayName(name);
            interactStatus.setItemMeta(interactStatusMeta);

            return interactStatus;
        }
    }
    private Location location1;
    private Location location2;
    private int height;
    private String name;
    private PlotSetting playerInteract = PlotSetting.DEACTIVATED;
    private PlotSetting playerBuild = PlotSetting.DEACTIVATED;
    private PlotSetting playerEnter = PlotSetting.DEACTIVATED;

    private ArrayList<String> playerInteractWhitelist = new ArrayList<>();
    private ArrayList<String> playerBuildWhitelist = new ArrayList<>();
    private ArrayList<String> playerEnterWhitelist = new ArrayList<>();



    public Plot() {

    }

    public Plot(Player player, Location location1, Location location2, int height, String name) {
        this.location1 = location1;
        this.location2 = location2;
        this.name = name;
        this.height = height;
        successfulCreation(player);
    }

    public static void showPlotHeightOptions(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, "Plots Options");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
            inventory.setItem(i + 27, glassPane);
        }
        for (int i = 1; i < 4; i++) {
            inventory.setItem(i * 9, glassPane);
            inventory.setItem(i * 9 + 8, glassPane);
        }

        String[] sizes = {"3", "5", "7", "10", "20", "30", "40", "50", "100", "infinite"};
        for (int i = 0; i < sizes.length; i++) {
            String size = sizes[i];
            ItemStack soilBlock = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta soilMeta = soilBlock.getItemMeta();
            soilMeta.setDisplayName(ChatColor.GREEN + "Height " + size);
            soilBlock.setItemMeta(soilMeta);

            int row = i / 7;
            int column = i % 7;
            int index = (row + 1) * 9 + column + 1;
            inventory.setItem(index, soilBlock);
        }

        player.openInventory(inventory);
    }


    private void successfulCreation(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
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
        player.sendMessage(ChatColor.GREEN + "Plot " + name + " created successfully !");
    }

    public PlotSetting getPlayerBuild() {
        return playerBuild;
    }

    public PlotSetting getPlayerEnter() {
        return playerEnter;
    }

    public PlotSetting getPlayerInteract() {
        return playerInteract;
    }

    public String getName() {
        return name;
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public int getHeight() {
        return height;
    }

    public void setPlayerBuildWhitelist(ArrayList<String> playerBuildWhitelist) {
        this.playerBuildWhitelist = playerBuildWhitelist;
    }

    public void setPlayerEnterWhitelist(ArrayList<String> playerEnterWhitelist) {
        this.playerEnterWhitelist = playerEnterWhitelist;
    }

    public void setPlayerInteractWhitelist(ArrayList<String> playerInteractWhitelist) {
        this.playerInteractWhitelist = playerInteractWhitelist;
    }

    public ArrayList<String> getPlayerBuildWhitelist() {
        return playerBuildWhitelist;
    }

    public ArrayList<String> getPlayerEnterWhitelist() {
        return playerEnterWhitelist;
    }

    public ArrayList<String> getPlayerInteractWhitelist() {
        return playerInteractWhitelist;
    }

    public boolean addPlayerToPlayerBuildWhitelist(String player) {
        if(playerBuildWhitelist.contains(player)) return false;
        return playerBuildWhitelist.add(player);
    }

    public boolean addPlayerToPlayerEnterWhitelist(String player) {
        if(playerEnterWhitelist.contains(player)) return false;
        return playerEnterWhitelist.add(player);
    }

    public boolean addPlayerToPlayerInteractWhitelist(String player) {
        if(playerInteractWhitelist.contains(player)) return false;
        return playerInteractWhitelist.add(player);
    }

    public boolean removePlayerToPlayerBuildWhitelist(String player) {
        return playerBuildWhitelist.remove(player);
    }

    public boolean removePlayerToPlayerEnterWhitelist(String player) {
        return playerEnterWhitelist.remove(player);
    }

    public boolean removePlayerToPlayerInteractWhitelist(String player) {
        return playerInteractWhitelist.remove(player);
    }

    public boolean addPlayerToWhitelist(String player, String whitelist) {
        if(whitelist.equals("buildWhitelist")) {
            return addPlayerToPlayerBuildWhitelist(player);
        }
        if(whitelist.equals("enterWhitelist")) {
            return addPlayerToPlayerEnterWhitelist(player);
        }
        if(whitelist.equals("interactWhitelist")) {
            return addPlayerToPlayerInteractWhitelist(player);
        }

        return false;
    }

    public boolean removePlayerToWhitelist(String player, String whitelist) {
        if(whitelist.equals("buildWhitelist")) {
            return removePlayerToPlayerBuildWhitelist(player);
        }
        if(whitelist.equals("enterWhitelist")) {
            return removePlayerToPlayerEnterWhitelist(player);
        }
        if(whitelist.equals("interactWhitelist")) {
            return removePlayerToPlayerInteractWhitelist(player);
        }

        return false;
    }

    public void setWhitelist(ArrayList<String> whitelist, String name) {
        if(name.equals("build")) {
            setPlayerBuildWhitelist(whitelist);
        }
        if(name.equals("enter")) {
            setPlayerEnterWhitelist(whitelist);
        }
        if(name.equals("interact")) {
            setPlayerInteractWhitelist(whitelist);
        }
    }

    public void loadFromConfig(ConfigurationSection config) {
        if (config.contains("name")) {
            name = config.getString("name");
        }
        if (config.contains("location1")) {
            location1 = config.getLocation("location1");
        }
        if (config.contains("location2")) {
            location2 = config.getLocation("location2");
        }
        if (config.contains("height")) {
            height = config.getInt("height");
        }
        if (config.contains("playerBuildWhitelist")) {
            playerBuildWhitelist = new ArrayList<>(config.getStringList("playerBuildWhitelist"));
        }
        if (config.contains("playerEnterWhitelist")) {
            playerEnterWhitelist = new ArrayList<>(config.getStringList("playerEnterWhitelist"));
        }
        if (config.contains("playerInteractWhitelist")) {
            playerInteractWhitelist = new ArrayList<>(config.getStringList("playerInteractWhitelist"));
        }
        if (config.contains("playerInteract")) {
            String pi = config.getString("playerInteract");
            playerInteract = PlotSetting.valueOf(pi);
        }
        if (config.contains("playerBuild")) {
            String pb = config.getString("playerBuild");
            playerBuild = PlotSetting.valueOf(pb);
        }
        if (config.contains("playerEnter")) {
            String pe = config.getString("playerEnter");
            playerEnter = PlotSetting.valueOf(pe);
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("name", name);
        config.set("location1", location1);
        config.set("location2", location2);
        config.set("height", height);
        config.set("playerBuildWhitelist", playerBuildWhitelist);
        config.set("playerEnterWhitelist", playerEnterWhitelist);
        config.set("playerInteractWhitelist", playerInteractWhitelist);
        config.set("playerInteract", playerInteract.toString());
        config.set("playerBuild", playerBuild.toString());
        config.set("playerEnter", playerEnter.toString());
    }

    private void showParticles(Player player) {
        int minX = (int) Math.floor(Math.min(location1.getX(), location2.getX()));
        int minY = (int) Math.floor(Math.min(location1.getY(), location2.getY()));
        int minZ = (int) Math.floor(Math.min(location1.getZ(), location2.getZ()));
        int maxX = (int) Math.ceil(Math.max(location1.getX(), location2.getX()));
        int maxZ = (int) Math.ceil(Math.max(location1.getZ(), location2.getZ()));

        int width = Math.abs(maxX - minX) + 1;
        int depth = Math.abs(maxZ - minZ) + 1;

        int h = height;
        if (height == -1) {
            h = 200;
        }
        int startHeight = minY;
        int endHeight = minY + h;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                if (x == 0 || x == width - 1 || z == 0 || z == depth - 1) {
                    for (int y = startHeight; y <= endHeight; y++) {
                        Location particleLocation = new Location(location1.getWorld(), minX + x, y, minZ + z);
                        player.spawnParticle(Particle.BUBBLE_COLUMN_UP, particleLocation, 1, 0, 0, 0);
                    }
                }
            }
        }
    }
    public void show(Main instance, Player player) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 80) {
                    this.cancel();
                    return;
                }

                showParticles(player);

                count++;
            }
        }.runTaskTimerAsynchronously(instance, 0L, 1);
    }

    private void setInteractOption(Inventory inventory) {
        ItemStack interactOption = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta interactOptionMeta = interactOption.getItemMeta();
        interactOptionMeta.setDisplayName("Players Can Interact");
        List<String> interactOptionLore = new ArrayList<>();
        interactOptionLore.add(getPlayerInteract().getName());
        interactOptionLore.add(ChatColor.WHITE + getWhitelistString(playerInteractWhitelist).toString());

        interactOptionMeta.setLore(interactOptionLore);
        interactOption.setItemMeta(interactOptionMeta);
        inventory.setItem(4, interactOption);

        ItemStack interactStatus = new ItemStack(getPlayerInteract().getItem());
        ItemMeta interactStatusMeta = interactStatus.getItemMeta();
        interactStatusMeta.setDisplayName(getPlayerInteract().getName());
        interactStatus.setItemMeta(interactStatusMeta);
        inventory.setItem(5, interactStatus);
    }

    private StringBuilder getWhitelistString(ArrayList<String> whitelist) {
        StringBuilder playerList = new StringBuilder(ChatColor.WHITE + "[");
        for (int i = 0; i < whitelist.size(); i++) {
            if (i != 0) {
                playerList.append(", ");
            }
            playerList.append(ChatColor.BOLD).append(whitelist.get(i));
        }
        playerList.append(ChatColor.WHITE + "]");

        return playerList;
    }

    private void setBuildOption(Inventory inventory) {
        ItemStack buildOption = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta buildOptionMeta = buildOption.getItemMeta();
        buildOptionMeta.setDisplayName("Players Can Build");
        List<String> buildOptionLore = new ArrayList<>();
        buildOptionLore.add(getPlayerBuild().getName());
        buildOptionLore.add(getWhitelistString(playerBuildWhitelist).toString());
        buildOptionMeta.setLore(buildOptionLore);
        buildOption.setItemMeta(buildOptionMeta);
        inventory.setItem(13, buildOption);

        ItemStack buildStatus = new ItemStack(getPlayerBuild().getItem());
        ItemMeta buildStatusMeta = buildStatus.getItemMeta();
        buildStatusMeta.setDisplayName(getPlayerBuild().getName());
        buildStatus.setItemMeta(buildStatusMeta);
        inventory.setItem(14, buildStatus);
    }

    private void setEnterOption(Inventory inventory) {
        ItemStack enterOption = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta enterOptionMeta = enterOption.getItemMeta();
        enterOptionMeta.setDisplayName("Players Can Enter");
        List<String> enterOptionLore = new ArrayList<>();
        enterOptionLore.add(getPlayerEnter().getName());
        enterOptionLore.add(getWhitelistString(playerEnterWhitelist).toString());
        enterOptionMeta.setLore(enterOptionLore);
        enterOption.setItemMeta(enterOptionMeta);
        inventory.setItem(22, enterOption);

        ItemStack enterStatus = new ItemStack(getPlayerEnter().getItem());
        ItemMeta enterStatusMeta = enterStatus.getItemMeta();
        enterStatusMeta.setDisplayName(getPlayerEnter().getName());
        enterStatus.setItemMeta(enterStatusMeta);
        inventory.setItem(23, enterStatus);
    }

    public void openSettingsInventory(Main instance, Player player) {
        player.setMetadata("setting", new FixedMetadataValue(instance, name));
        Inventory inventory = instance.getServer().createInventory(null, 27, name + " plot settings");

        ItemStack grassBlock = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta grassBlockMeta = grassBlock.getItemMeta();
        grassBlockMeta.setDisplayName(ChatColor.GREEN + "Plot " + getName());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Location1: X: " + getLocation1().getX() +
                " Y: " + getLocation1().getY() +
                " Z: " + getLocation1().getZ());
        lore.add(ChatColor.YELLOW + "Location2: X: " + getLocation2().getX() +
                " Y: " + getLocation2().getY() +
                " Z: " + getLocation2().getZ());
        lore.add(ChatColor.YELLOW + "Height: " + getHeight());

        grassBlockMeta.setLore(lore);
        grassBlock.setItemMeta(grassBlockMeta);

        inventory.setItem(0, grassBlock);

        // Players Can Interact Option
        setInteractOption(inventory);

        // Players Can Build Option
        setBuildOption(inventory);

        // Players Can Enter Option
        setEnterOption(inventory);

        player.openInventory(inventory);
    }


    public void toggleInteractStatus(Main instance, Player player, Inventory inventory, ItemStack item, int slot) {
        if (item.getType() == Material.BARRIER) {
            if(slot == 5) {
                playerInteract = PlotSetting.ACTIVATED;
                setInteractOption(inventory);
            }
            if(slot == 14) {
                playerBuild = PlotSetting.ACTIVATED;
                setBuildOption(inventory);
            }
            if(slot==23) {
                playerEnter = PlotSetting.ACTIVATED;
                setEnterOption(inventory);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else if (item.getType() == Material.SLIME_BALL) {
            if(slot == 5) {
                playerInteract = PlotSetting.CUSTOM;
                setInteractOption(inventory);
            }
            if(slot == 14) {
                playerBuild = PlotSetting.CUSTOM;
                setBuildOption(inventory);
            }
            if(slot==23) {
                playerEnter = PlotSetting.CUSTOM;
                setEnterOption(inventory);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else if (item.getType() == Material.EMERALD) {
            if(slot == 5) {
                playerInteract = PlotSetting.DEACTIVATED;
                setInteractOption(inventory);
            }
            if(slot == 14) {
                playerBuild = PlotSetting.DEACTIVATED;
                setBuildOption(inventory);
            }
            if(slot==23) {
                playerEnter = PlotSetting.DEACTIVATED;
                setEnterOption(inventory);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else if (item.getType() == Material.WRITABLE_BOOK) {
            if(slot == 4) {
                player.setMetadata("whitelist", new FixedMetadataValue(instance, "interact"));
                sendWhiteListSetting(player, "interact");
            }
            if(slot == 13) {
                player.setMetadata("whitelist", new FixedMetadataValue(instance, "build"));
                sendWhiteListSetting(player, "build");
            }
            if(slot==22) {
                player.setMetadata("whitelist", new FixedMetadataValue(instance, "enter"));
                sendWhiteListSetting(player, "enter");
            }
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
        }
    }

    public void sendWhiteListSetting(Player player, String type) {
        ArrayList<String> whitelist = new ArrayList<>();
        if(type.equals("interact")) {
            whitelist = getPlayerInteractWhitelist();
        }
        if(type.equals("build")) {
            whitelist = getPlayerBuildWhitelist();
        }
        if(type.equals("enter")) {
            whitelist = getPlayerEnterWhitelist();
        }

        StringBuilder playerList = getWhitelistString(whitelist);

        player.sendMessage(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "Players can " + type + " whitelist:");
        player.sendMessage(playerList.toString());

        // clickable messages
        String addPlayerMessage = ChatColor.GOLD + "[add player]";
        String removePlayerMessage = ChatColor.GOLD + "[remove player]";
        String command = "/plot settings " + this.name + " " + type + "Whitelist ";

        player.spigot().sendMessage(
                Messages.createClickableMessage(addPlayerMessage, command + "add ")
        );
        player.spigot().sendMessage(
                Messages.createClickableMessage(removePlayerMessage, command + "remove ")
        );

        player.closeInventory();
    }
}
