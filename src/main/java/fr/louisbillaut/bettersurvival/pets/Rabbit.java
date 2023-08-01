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

public class Rabbit extends Pet {
    private void setFields() {
        price = 15000;
        ItemStack egg = new ItemStack(Material.RABBIT_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Rabbit");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
    }
    public Rabbit() {
        setFields();
    }
    public Rabbit(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Rabbit rabbit = (org.bukkit.entity.Rabbit) owner.getWorld().spawnEntity(location, EntityType.RABBIT);
        rabbit.setInvulnerable(true);
        rabbit.setGravity(false);
        rabbit.setSilent(true);
        rabbit.setBreed(false);
        rabbit.setAgeLock(true);
        rabbit.setAI(false);

        rabbit.addScoreboardTag(invulnerableTag);

        entities.add(rabbit);

        startFollowTask();
    }
}
