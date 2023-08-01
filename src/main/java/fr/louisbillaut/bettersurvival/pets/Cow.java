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

public class Cow extends Pet {
    public Cow(Main instance, Player owner) {
        super(instance, owner);
        price = 10000;
        ItemStack cowEgg = new ItemStack(Material.COW_SPAWN_EGG);
        ItemMeta itemMeta = cowEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Cow");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        cowEgg.setItemMeta(itemMeta);
        item = new ItemStack(cowEgg);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Cow cow = (org.bukkit.entity.Cow) owner.getWorld().spawnEntity(location, EntityType.COW);
        cow.setBaby();
        cow.setInvulnerable(true);
        cow.setGravity(false);
        cow.setSilent(true);
        cow.setBreed(false);
        cow.setAgeLock(true);
        cow.setAI(false);

        cow.addScoreboardTag(invulnerableTag);

        entities.add(cow);

        startFollowTask();
    }
}
