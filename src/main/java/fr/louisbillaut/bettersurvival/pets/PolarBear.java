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

public class PolarBear extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.POLAR_BEAR_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Polar bear");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
    }
    public PolarBear() {
        setFields();
    }
    public PolarBear(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.PolarBear polarBear = (org.bukkit.entity.PolarBear) owner.getWorld().spawnEntity(location, EntityType.POLAR_BEAR);
        polarBear.setBaby();
        polarBear.setInvulnerable(true);
        polarBear.setGravity(false);
        polarBear.setSilent(true);
        polarBear.setBreed(false);
        polarBear.setAgeLock(true);
        polarBear.setAI(false);

        polarBear.addScoreboardTag(invulnerableTag);

        entities.add(polarBear);

        startFollowTask(owner);
    }
}
