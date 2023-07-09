package fr.louisbillaut.bettersurvival.game;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Trade {
    private ItemStack itemsToBuy;
    private List<ItemStack> itemsToExchange;

    public Trade(ItemStack itemsToBuy, List<ItemStack> itemsToExchange) {
        this.itemsToBuy = itemsToBuy;
        this.itemsToExchange = itemsToExchange;
    }

    public ItemStack getItemsToBuy() {
        return itemsToBuy;
    }

    public List<ItemStack> getItemsToExchange() {
        return itemsToExchange;
    }
}
