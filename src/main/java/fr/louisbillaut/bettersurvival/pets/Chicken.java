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

public class Chicken extends Pet {
    public Chicken(Main instance, Player owner) {
        super(instance, owner);
        price = 10000;
        ItemStack chickenEgg = new ItemStack(Material.CHICKEN_SPAWN_EGG);
        ItemMeta itemMeta = chickenEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Chicken");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        chickenEgg.setItemMeta(itemMeta);
        item = new ItemStack(chickenEgg);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Chicken chicken = (org.bukkit.entity.Chicken) owner.getWorld().spawnEntity(location, EntityType.CHICKEN);
        chicken.setInvulnerable(true);
        chicken.setGravity(false);
        chicken.setSilent(true);
        chicken.setBreed(false);
        chicken.setAgeLock(true);
        chicken.setAI(false);

        chicken.addScoreboardTag(invulnerableTag);

        entities.add(chicken);

        startFollowTask();
    }
}
