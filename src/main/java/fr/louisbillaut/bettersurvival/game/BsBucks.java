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
    public static double blockPrice = 0.20;
    public static double PlotNbOfBlocToIncreasePrice = 27000;
    public static double PercentageAugmentation = 0.17;
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

    private ItemStack getSharpnessBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.DAMAGE_ALL, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getPowerBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getEfficiencyBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.DIG_SPEED, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getFortuneBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getLootingBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getThornsBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.THORNS, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getFireAspectBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private ItemStack getSilkTouchBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        bookMeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, true);
        enchantedBook.setItemMeta(bookMeta);
        return enchantedBook;
    }

    private List<BSItem> itemsToSale = new ArrayList<>(List.of(
            new BSItem(new ItemStack(Material.DIAMOND_BLOCK), 360),
            new BSItem(getSpeedPotion(), 50),
            new BSItem(new ItemStack(Material.ELYTRA), 6000),
            new BSItem(new ItemStack(Material.CREEPER_HEAD), 2500),
            new BSItem(new ItemStack(Material.PIGLIN_HEAD), 3000),
            new BSItem(new ItemStack(Material.ZOMBIE_HEAD), 2000),
            new BSItem(new ItemStack(Material.RESPAWN_ANCHOR), 1950),
            new BSItem(new ItemStack(Material.TURTLE_HELMET), 3000),
            new BSItem(getRiptideTrident(), 2500),
            new BSItem(new ItemStack(Material.ENDER_CHEST), 900),
            new BSItem(new ItemStack(Material.SPONGE), 230),
            new BSItem(new ItemStack(Material.BEACON), 5500),
            new BSItem(new ItemStack(Material.GOLDEN_CARROT), 30),
            new BSItem(new ItemStack(Material.NETHERITE_INGOT), 2000),
            new BSItem(new ItemStack(Material.CAKE), 50),
            new BSItem(new ItemStack(Material.EXPERIENCE_BOTTLE), 800),
            new BSItem(new ItemStack(Material.ENDER_PEARL), 10),
            new BSItem(getPoisonPotion(), 55),
            new BSItem(new ItemStack(Material.SLIME_BLOCK), 300),
            new BSItem(new ItemStack(Material.SPYGLASS), 35),
            new BSItem(getFireResistancePotion(), 100),
            new BSItem(new ItemStack(Material.MELON_SEEDS), 6),
            new BSItem(new ItemStack(Material.SADDLE), 500),
            new BSItem(new ItemStack(Material.FIRE_CHARGE), 70),
            new BSItem(getRegenerationPotion(), 80),
            new BSItem(new ItemStack(Material.GOLDEN_APPLE), 210),
            new BSItem(new ItemStack(Material.ENDER_EYE), 60),
            new BSItem(new ItemStack(Material.HONEY_BOTTLE), 40),
            new BSItem(new ItemStack(Material.PUFFERFISH), 40),
            new BSItem(new ItemStack(Material.COOKED_CHICKEN), 15),
            new BSItem(new ItemStack(Material.COOKED_PORKCHOP), 20),
            new BSItem(new ItemStack(Material.TNT), 55),
            new BSItem(new ItemStack(Material.HEART_OF_THE_SEA), 700),
            new BSItem(new ItemStack(Material.PRISMARINE_CRYSTALS), 50),
            new BSItem(new ItemStack(Material.NAUTILUS_SHELL), 400),
            new BSItem(new ItemStack(Material.SALMON_BUCKET), 90),
            new BSItem(new ItemStack(Material.TORCHFLOWER), 300),
            new BSItem(new ItemStack(Material.PITCHER_PLANT), 300),
            new BSItem(getInfinityBook(), 400),
            new BSItem(getSharpnessBook(), 100),
            new BSItem(getPowerBook(), 100),
            new BSItem(getEfficiencyBook(), 100),
            new BSItem(getFortuneBook(), 450),
            new BSItem(getLootingBook(), 450),
            new BSItem(getThornsBook(), 300),
            new BSItem(getFireAspectBook(), 250),
            new BSItem(getSilkTouchBook(), 550)
    ));

    public boolean containsItem(ItemStack itemStack) {
        for(BSItem bsItem: itemsToSale) {
            if (bsItem.getItem().isSimilar(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public List<BSItem> getItemsToSale() {
        return itemsToSale;
    }
}
