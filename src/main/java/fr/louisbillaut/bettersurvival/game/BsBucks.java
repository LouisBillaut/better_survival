package fr.louisbillaut.bettersurvival.game;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class BsBucks {
    public class BSItem {
        private int price;
        private ItemStack item;

        public BSItem(ItemStack item, int price) {
            this.item = item;
            this.price = price;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }
    }

    private ItemStack getSpeedPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        potionMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
        potion.setItemMeta(potionMeta);

        return potion;
    }

    private ItemStack getInfinityBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.ARROW_INFINITE, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    public ItemStack getRiptideTrident() {
        ItemStack trident = new ItemStack(Material.TRIDENT);
        ItemMeta meta = trident.getItemMeta();
        meta.addEnchant(Enchantment.RIPTIDE, 1, true);
        trident.setItemMeta(meta);
        return trident;
    }

    private ItemStack getPoisonPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        potionMeta.setBasePotionData(new PotionData(PotionType.POISON, false, true));
        potion.setItemMeta(potionMeta);

        return potion;
    }

    private ItemStack getFireResistancePotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        potionMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE, false, false));
        potion.setItemMeta(potionMeta);

        return potion;
    }

    private ItemStack getRegenerationPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        potionMeta.setBasePotionData(new PotionData(PotionType.REGEN, false, true));
        potion.setItemMeta(potionMeta);

        return potion;
    }

    private List<BSItem> itemsToSale = new ArrayList<>(List.of(
            new BSItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 120),
            new BSItem(getSpeedPotion(), 30),
            new BSItem(new ItemStack(Material.ELYTRA), 8000),
            new BSItem(getInfinityBook(), 100),
            new BSItem(new ItemStack(Material.CREEPER_HEAD), 500),
            new BSItem(new ItemStack(Material.PIGLIN_HEAD), 700),
            new BSItem(new ItemStack(Material.ZOMBIE_HEAD), 500),
            new BSItem(new ItemStack(Material.SHULKER_BOX), 100),
            new BSItem(new ItemStack(Material.RESPAWN_ANCHOR), 500),
            new BSItem(getInfinityBook(), 200),
            new BSItem(getRiptideTrident(), 1300),
            new BSItem(new ItemStack(Material.ENDER_CHEST), 100),
            new BSItem(new ItemStack(Material.SPONGE), 70),
            new BSItem(new ItemStack(Material.BEACON), 3000),
            new BSItem(new ItemStack(Material.EMERALD), 30),
            new BSItem(new ItemStack(Material.GOLDEN_CARROT), 20),
            new BSItem(new ItemStack(Material.NETHERITE_INGOT), 700),
            new BSItem(new ItemStack(Material.CAKE), 60),
            new BSItem(new ItemStack(Material.EXPERIENCE_BOTTLE), 20),
            new BSItem(new ItemStack(Material.ENDER_PEARL), 40),
            new BSItem(getPoisonPotion(), 50),
            new BSItem(new ItemStack(Material.SLIME_BLOCK), 30),
            new BSItem(getFireResistancePotion(), 60),
            new BSItem(new ItemStack(Material.MELON_SEEDS), 5),
            new BSItem(new ItemStack(Material.SADDLE), 500),
            new BSItem(new ItemStack(Material.FIRE_CHARGE), 30),
            new BSItem(getRegenerationPotion(), 70),
            new BSItem(new ItemStack(Material.ICE), 20),
            new BSItem(new ItemStack(Material.GOLDEN_APPLE), 60),
            new BSItem(new ItemStack(Material.ENDER_EYE), 30),
            new BSItem(new ItemStack(Material.NETHERITE_INGOT), 700),
            new BSItem(new ItemStack(Material.HONEY_BOTTLE), 10),
            new BSItem(new ItemStack(Material.TOTEM_OF_UNDYING), 1000),
            new BSItem(new ItemStack(Material.COOKED_CHICKEN), 5),
            new BSItem(new ItemStack(Material.COOKED_PORKCHOP), 8),
            new BSItem(new ItemStack(Material.TNT), 40),
            new BSItem(new ItemStack(Material.PRISMARINE_CRYSTALS), 15),
            new BSItem(new ItemStack(Material.NAUTILUS_SHELL), 30),
            new BSItem(new ItemStack(Material.SALMON_BUCKET), 30),
            new BSItem(new ItemStack(Material.TORCHFLOWER), 90),
            new BSItem(new ItemStack(Material.PITCHER_PLANT), 90)
    ));

    public List<BSItem> getItemsToSale() {
        return itemsToSale;
    }
}
