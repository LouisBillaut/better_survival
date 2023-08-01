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

public class Sheep extends Pet {
    public Sheep(Main instance, Player owner) {
        super(instance, owner);
        price = 10000;
        ItemStack egg = new ItemStack(Material.SHEEP_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Sheep");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Sheep sheep = (org.bukkit.entity.Sheep) owner.getWorld().spawnEntity(location, EntityType.SHEEP);
        sheep.setBaby();
        sheep.setInvulnerable(true);
        sheep.setGravity(false);
        sheep.setSilent(true);
        sheep.setBreed(false);
        sheep.setAgeLock(true);
        sheep.setAI(false);

        sheep.addScoreboardTag(invulnerableTag);

        entities.add(sheep);

        startFollowTask();
    }
}
