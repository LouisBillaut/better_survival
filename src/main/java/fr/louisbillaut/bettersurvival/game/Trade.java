package fr.louisbillaut.bettersurvival.game;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Trade {
    ItemStack itemsToBuy;
    List<ItemStack> itemsToExchange;

    public Trade(ItemStack itemsToBuy, List<ItemStack> itemsToExchange) {
        this.itemsToBuy = itemsToBuy;
        this.itemsToExchange = itemsToExchange;
    }
}
