package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Pig extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.PIG_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Pig");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
    }
    public Pig() {
        setFields();
    }
    public Pig(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Pig pig = (org.bukkit.entity.Pig) owner.getWorld().spawnEntity(location, EntityType.PIG);
        pig.setBaby();
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setSilent(true);
        pig.setBreed(false);
        pig.setAgeLock(true);
        pig.setAI(false);

        pig.addScoreboardTag(invulnerableTag);

        entities.add(pig);

        startFollowTask(owner);
    }
}
