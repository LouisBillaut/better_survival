package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.animations.Animation;
import fr.louisbillaut.bettersurvival.pets.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static fr.louisbillaut.bettersurvival.animations.Animation.createAnimationItem;
import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;
import static fr.louisbillaut.bettersurvival.pets.Pet.backItem;
import static fr.louisbillaut.bettersurvival.pets.Pet.createPetItem;

public class Cosmetics {
    List<Pet> pets = new ArrayList<>();
    List<Animation> animations = new ArrayList<>();
    Animation activeAnimation;
    Pet activePet;

    public Cosmetics() {}

    public boolean addPet(Pet pet) {
        return pets.add(pet);
    }
    public boolean addAnimation(Animation animation) {
        return animations.add(animation);
    }

    public List<Pet> getPets() {
        return pets;
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public void saveToConfig(ConfigurationSection config) {
        List<String> configPets = new ArrayList<>();
        for(Pet p: pets) {
            configPets.add(p.getName());
        }
        config.set("pets", configPets);

        List<String> configAnimations = new ArrayList<>();
        for(Animation p: animations) {
            configAnimations.add(p.getName());
        }
        config.set("animations", configAnimations);
    }

    public void loadFromConfig(Main instance, ConfigurationSection config) {
        if (config.contains("pets")) {
            List<String> configPets = config.getStringList("pets");
            for (String name: configPets) {
                Pet p = Pet.getPetFromOriginalName(name);
                if (p == null) continue;
                pets.add(p);
            }
        }
        if (config.contains("animations")) {
            List<String> configAnimations = config.getStringList("animations");
            for (String name: configAnimations) {
                Animation p = Animation.getAnimationFromOriginalName(name);
                if (p == null) continue;
                animations.add(p);
            }
        }
    }

    public static void displayCosmeticsShop(Player player) {
        if (player.getBukkitPlayer() == null) return;
        Inventory inventory = Bukkit.createInventory(null, 27, "bs shop");

        for (int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var animationsHead = createAnimationItem();
        var sniffer = createPetItem();

        inventory.setItem(12, animationsHead);
        inventory.setItem(14, sniffer);

        player.getBukkitPlayer().openInventory(inventory);
    }

    public void displayOwnedPets(Player player) {
        if (player.getBukkitPlayer() == null) return;

        Inventory inventory = Bukkit.createInventory(null, 54, "Your pets");

        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }
        var sniffer = createPetItem();
        inventory.setItem(0, sniffer);

        int index = 10;
        for(var i = 0; i < pets.size(); i++) {
            if (pets.get(i).isSecret()) {
                index --;
                continue;
            }
            var item = pets.get(i);
            var itemStack = item.getItem().clone();
            var itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);
            if((index + i) == 16 || (index + i) == 25 || (index + i) == 34 || (index + i) == 43) {
                inventory.setItem(index + i, itemStack);
                index += 2;
            } else {
                inventory.setItem(index + i, itemStack);
            }
        }

        inventory.setItem(45, backItem());
        player.getBukkitPlayer().openInventory(inventory);
    }

    public void displayOwnedAnimations(Player player) {
        if (player.getBukkitPlayer() == null) return;

        Inventory inventory = Bukkit.createInventory(null, 54, "Your animations");

        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }
        var animationsHead = createAnimationItem();
        inventory.setItem(0, animationsHead);

        int index = 10;
        for(var i = 0; i < animations.size(); i++) {
            var item = animations.get(i);
            var itemStack = item.getItem().clone();
            var itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);
            if((index + i) == 16 || (index + i) == 25 || (index + i) == 34 || (index + i) == 43) {
                inventory.setItem(index + i, itemStack);
                index += 2;
            } else {
                inventory.setItem(index + i, itemStack);
            }
        }

        inventory.setItem(45, backItem());
        player.getBukkitPlayer().openInventory(inventory);
    }

    public void displayEquipAnimation(Player player, ItemStack itemStack) {
        if (player.getBukkitPlayer() == null) return;

        var itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(new ArrayList<>());
        itemStack.setItemMeta(itemMeta);
        Inventory inventory = Bukkit.createInventory(null, 27, "Equip animation");
        setEquipInv(player, itemStack, inventory);
    }

    public void displayEquipPet(Player player, ItemStack itemStack) {
        if (player.getBukkitPlayer() == null) return;

        var itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(new ArrayList<>());
        itemStack.setItemMeta(itemMeta);
        Inventory inventory = Bukkit.createInventory(null, 27, "Equip pet");
        setEquipInv(player, itemStack, inventory);
    }

    private void setEquipInv(Player player, ItemStack itemStack, Inventory inventory) {
        for (int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        inventory.setItem(12, itemStack);
        inventory.setItem(14, createEquipItem());
        inventory.setItem(18, backItem());
        player.getBukkitPlayer().openInventory(inventory);
    }

    private static ItemStack createEquipItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "equip");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }
}
