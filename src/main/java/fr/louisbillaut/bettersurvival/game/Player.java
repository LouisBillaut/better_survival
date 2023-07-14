package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private List<Plot> plots;
    private List<Shop> shops = new ArrayList<>();
    private String playerName;

    private static int maxShops = 5;

    private org.bukkit.entity.Player bukkitPlayer;

    public Player(String playerName) {
        this.playerName = playerName;
        plots = new ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                bukkitPlayer = p;
            }
        }
    }

    public void setBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Plot getPlot(String name) {
        for(Plot p: plots) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void addPlot(Plot zone) {
        plots.add(zone);
    }

    public void addShop(Shop shop) {
        shops.add(shop);
    }

    public static int getMaxShops() {
        return maxShops;
    }

    public Shop getShop(String name) {
        for(Shop s: shops) {
            if(s.getName().equals(name)) {
                return s;
            }
        }

        return null;
    }

    public void removeShop(String name) {
        for(var i = 0; i < shops.size(); i ++) {
            if(shops.get(i).getName().equals(name)){
                shops.remove(i);
            }
        }
    }

    public void removePlot(Plot zone) {
        plots.remove(zone);
    }

    public List<Plot> getPlots() {
        return plots;
    }

    public org.bukkit.entity.Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public ConfigurationSection getPlayerConfigByName(String playerName, ConfigurationSection playersSection) {
        for (String playerKey : playersSection.getKeys(false)) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(playerKey);
            String name = playerSection.getString("name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return playerSection;
            }
        }
        return null;
    }

    public void loadFromConfig(ConfigurationSection config) {
        ConfigurationSection c = getPlayerConfigByName(this.playerName, config);
        if (c == null) {
            return;
        }
        if (c.contains("plots")) {
            for (String plotKey : Objects.requireNonNull(c.getConfigurationSection("plots")).getKeys(false)) {
                Plot plot = new Plot();
                plot.loadFromConfig(Objects.requireNonNull(c.getConfigurationSection("plots." + plotKey)));
                plots.add(plot);
            }
        }
        if (c.contains("shops")) {
            ConfigurationSection shopsSection = c.getConfigurationSection("shops");
            for (String shopKey : shopsSection.getKeys(false)) {
                Shop shop = new Shop();
                shop.loadFromConfig(Objects.requireNonNull(shopsSection.getConfigurationSection(shopKey)));
                shops.add(shop);
            }
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("name", playerName);

        ConfigurationSection plotsSection = config.createSection("plots");
        for (int i = 0; i < plots.size(); i++) {
            Plot plot = plots.get(i);
            ConfigurationSection plotSection = plotsSection.createSection(String.valueOf(i));
            plotSection.set("name", plot.getName());
            plot.saveToConfig(config.createSection("plots." + i));
        }

        ConfigurationSection shopsSection = config.createSection("shops");
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
            ConfigurationSection shopSection = shopsSection.createSection(String.valueOf(i));
            shop.saveToConfig(shopSection);
        }
    }

    public void displayListShopInventory() {
        int inventorySize = Math.max(9, (int) Math.ceil(shops.size() / 9.0) * 9);
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Shops List");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneMeta = glassPane.getItemMeta();
        glassPaneMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassPaneMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
            inventory.setItem(inventorySize - 9 + i, glassPane);
        }

        for (int i = 0; i < shops.size(); i++) {
            ItemStack villagerHead = Head.getCustomHead(Head.villagerHead);
            ItemMeta villagerHeadMeta = villagerHead.getItemMeta();
            villagerHeadMeta.setDisplayName(ChatColor.GREEN + shops.get(i).getName());
            villagerHead.setItemMeta(villagerHeadMeta);
            int row = i / 9;
            int column = i % 9;

            inventory.setItem(row * 9 + column, villagerHead);
        }

        bukkitPlayer.openInventory(inventory);
    }

    public void displayListPlotInventory() {
        int inventorySize = Math.max(9, (int) Math.ceil(plots.size() / 9.0) * 9);
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Plots List");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneMeta = glassPane.getItemMeta();
        glassPaneMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassPaneMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
            inventory.setItem(inventorySize - 9 + i, glassPane);
        }

        for (int i = 0; i < plots.size(); i++) {
            Plot plot = plots.get(i);

            ItemStack grassBlock = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta grassBlockMeta = grassBlock.getItemMeta();
            grassBlockMeta.setDisplayName(ChatColor.GREEN + "Plot " + plot.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Location1: X: " + plot.getLocation1().getX() +
                    " Y: " + plot.getLocation1().getY() +
                    " Z: " + plot.getLocation1().getZ());
            lore.add(ChatColor.YELLOW + "Location2: X: " + plot.getLocation2().getX() +
                    " Y: " + plot.getLocation2().getY() +
                    " Z: " + plot.getLocation2().getZ());
            lore.add(ChatColor.YELLOW + "Height: " + plot.getHeight());

            grassBlockMeta.setLore(lore);
            grassBlock.setItemMeta(grassBlockMeta);

            int row = i / 9;
            int column = i % 9;

            inventory.setItem(row * 9 + column, grassBlock);
        }

        bukkitPlayer.openInventory(inventory);
    }

}
