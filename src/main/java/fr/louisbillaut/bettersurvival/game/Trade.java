package fr.louisbillaut.bettersurvival.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    private ItemStack itemsToBuy;
    private List<ItemStack> itemsToExchange;

    public Trade(ItemStack itemsToBuy, List<ItemStack> itemsToExchange) {
        this.itemsToBuy = itemsToBuy;
        this.itemsToExchange = itemsToExchange;
    }

    public Trade() {}

    public ItemStack getItemsToBuy() {
        return itemsToBuy;
    }

    public List<ItemStack> getItemsToExchange() {
        return itemsToExchange;
    }

    public void loadFromConfig(ConfigurationSection config) {
        if (config.contains("itemsToBuy")) {
            itemsToBuy = config.getItemStack("itemsToBuy");
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

        ConfigurationSection exchangeSection = config.createSection("itemsToExchange");
        for (int i = 0; i < itemsToExchange.size(); i++) {
            ItemStack item = itemsToExchange.get(i);
            exchangeSection.set(String.valueOf(i), item);
        }
    }
}
