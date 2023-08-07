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

    private void setFields() {
        price = 10000;
        ItemStack cowEgg = new ItemStack(Material.COW_SPAWN_EGG);
        ItemMeta itemMeta = cowEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Cow");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        cowEgg.setItemMeta(itemMeta);
        item = new ItemStack(cowEgg);
        name = "cow";
    }
    public Cow() {
        super();
        setFields();
    }
    public Cow(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Cow cow = (org.bukkit.entity.Cow) owner.getWorld().spawnEntity(location, EntityType.COW);
        cow.setBaby();
        cow.setInvulnerable(true);
        cow.setGravity(false);
        cow.setSilent(true);
        cow.setBreed(false);
        cow.setAgeLock(true);
        cow.setAI(false);

        if (customName != null) {
            cow.setCustomName(customName);
            cow.setCustomNameVisible(true);
        }

        cow.addScoreboardTag(invulnerableTag);

        entities.add(cow);

        startFollowTask(instance, owner);
    }
}
