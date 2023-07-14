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
    public static double blockPrice = 12;
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
            new BSItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 360),
            new BSItem(getSpeedPotion(), 90),
            new BSItem(new ItemStack(Material.ELYTRA), 24000),
            new BSItem(getInfinityBook(), 300),
            new BSItem(new ItemStack(Material.CREEPER_HEAD), 1500),
            new BSItem(new ItemStack(Material.PIGLIN_HEAD), 2100),
            new BSItem(new ItemStack(Material.ZOMBIE_HEAD), 1500),
            new BSItem(new ItemStack(Material.SHULKER_BOX), 300),
            new BSItem(new ItemStack(Material.RESPAWN_ANCHOR), 1500),
            new BSItem(getInfinityBook(), 600),
            new BSItem(getRiptideTrident(), 3900),
            new BSItem(new ItemStack(Material.ENDER_CHEST), 300),
            new BSItem(new ItemStack(Material.SPONGE), 210),
            new BSItem(new ItemStack(Material.BEACON), 9000),
            new BSItem(new ItemStack(Material.EMERALD), 90),
            new BSItem(new ItemStack(Material.GOLDEN_CARROT), 60),
            new BSItem(new ItemStack(Material.NETHERITE_INGOT), 2100),
            new BSItem(new ItemStack(Material.CAKE), 180),
            new BSItem(new ItemStack(Material.EXPERIENCE_BOTTLE), 60),
            new BSItem(new ItemStack(Material.ENDER_PEARL), 120),
            new BSItem(getPoisonPotion(), 150),
            new BSItem(new ItemStack(Material.SLIME_BLOCK), 90),
            new BSItem(getFireResistancePotion(), 180),
            new BSItem(new ItemStack(Material.MELON_SEEDS), 15),
            new BSItem(new ItemStack(Material.SADDLE), 1500),
            new BSItem(new ItemStack(Material.FIRE_CHARGE), 90),
            new BSItem(getRegenerationPotion(), 210),
            new BSItem(new ItemStack(Material.ICE), 60),
            new BSItem(new ItemStack(Material.GOLDEN_APPLE), 180),
            new BSItem(new ItemStack(Material.ENDER_EYE), 90),
            new BSItem(new ItemStack(Material.NETHERITE_INGOT), 2100),
            new BSItem(new ItemStack(Material.HONEY_BOTTLE), 30),
            new BSItem(new ItemStack(Material.TOTEM_OF_UNDYING), 3000),
            new BSItem(new ItemStack(Material.COOKED_CHICKEN), 15),
            new BSItem(new ItemStack(Material.COOKED_PORKCHOP), 24),
            new BSItem(new ItemStack(Material.TNT), 120),
            new BSItem(new ItemStack(Material.PRISMARINE_CRYSTALS), 45),
            new BSItem(new ItemStack(Material.NAUTILUS_SHELL), 90),
            new BSItem(new ItemStack(Material.SALMON_BUCKET), 90),
            new BSItem(new ItemStack(Material.TORCHFLOWER), 270),
            new BSItem(new ItemStack(Material.PITCHER_PLANT), 270)
    ));

    public List<BSItem> getItemsToSale() {
        return itemsToSale;
    }
}
