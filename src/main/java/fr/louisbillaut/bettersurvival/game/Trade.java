package fr.louisbillaut.bettersurvival.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    private ItemStack itemsToBuy;
    private List<ItemStack> itemsToExchange;
    private int maxTrade = 1;

    public Trade(ItemStack itemsToBuy, List<ItemStack> itemsToExchange, int maxTrade) {
        this.itemsToBuy = itemsToBuy;
        this.itemsToExchange = itemsToExchange;
        this.maxTrade = maxTrade;
    }

    public Trade() {}

    public ItemStack getItemsToBuy() {
        return itemsToBuy;
    }

    public List<ItemStack> getItemsToExchange() {
        return itemsToExchange;
    }

    public void setMaxTrade(int maxTrade) {
        this.maxTrade = maxTrade;
    }

    public int getMaxTrade() {
        return maxTrade;
    }

    public void loadFromConfig(ConfigurationSection config) {
        if (config.contains("itemsToBuy")) {
            itemsToBuy = config.getItemStack("itemsToBuy");
        }
        if (config.contains("maxTrade")) {
            maxTrade = config.getInt("maxTrade");
        }
        if (config.contains("itemsToExchange")) {
            ConfigurationSection exchangeSection = config.getConfigurationSection("itemsToExchange");
            itemsToExchange = new ArrayList<>();
            for (String key : exchangeSection.getKeys(false)) {
                ItemStack item = exchangeSection.getItemStack(key);
                itemsToExchange.add(item);
            }
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("itemsToBuy", itemsToBuy);
        config.set("maxTrade", maxTrade);
        ConfigurationSection exchangeSection = config.createSection("itemsToExchange");
        for (int i = 0; i < itemsToExchange.size(); i++) {
            ItemStack item = itemsToExchange.get(i);
            exchangeSection.set(String.valueOf(i), item);
        }
    }
}
