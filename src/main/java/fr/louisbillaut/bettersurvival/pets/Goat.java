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

public class Goat extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack goatEgg = new ItemStack(Material.GOAT_SPAWN_EGG);
        ItemMeta itemMeta = goatEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Goat");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        goatEgg.setItemMeta(itemMeta);
        item = new ItemStack(goatEgg);
    }
    public Goat() {
        setFields();
    }
    public Goat(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Goat goat = (org.bukkit.entity.Goat) owner.getWorld().spawnEntity(location, EntityType.GOAT);
        goat.setBaby();
        goat.setInvulnerable(true);
        goat.setGravity(false);
        goat.setSilent(true);
        goat.setBreed(false);
        goat.setAgeLock(true);
        goat.setAI(false);

        goat.addScoreboardTag(invulnerableTag);

        entities.add(goat);

        startFollowTask();
    }
}
