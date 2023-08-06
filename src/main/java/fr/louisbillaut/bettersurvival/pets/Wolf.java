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

public class Wolf extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.WOLF_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Wolf");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
        name = "wolf";
    }
    public Wolf() {
        setFields();
    }
    public Wolf(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) owner.getWorld().spawnEntity(location, EntityType.WOLF);
        wolf.setBaby();
        wolf.setInvulnerable(true);
        wolf.setGravity(false);
        wolf.setSilent(true);
        wolf.setBreed(false);
        wolf.setAgeLock(true);
        wolf.setAI(false);

        wolf.addScoreboardTag(invulnerableTag);

        entities.add(wolf);

        startFollowTask(owner);
    }
}
