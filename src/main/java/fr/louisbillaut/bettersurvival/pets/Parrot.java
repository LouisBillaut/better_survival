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

public class Parrot extends Pet {

    private void setFields() {
        price = 15000;
        ItemStack egg = new ItemStack(Material.PARROT_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Parrot");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
        name = "parrot";
    }
    public Parrot() {
        setFields();
    }
    public Parrot(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Parrot parrot = (org.bukkit.entity.Parrot) owner.getWorld().spawnEntity(location, EntityType.PARROT);
        parrot.setBaby();
        parrot.setInvulnerable(true);
        parrot.setGravity(false);
        parrot.setSilent(true);
        parrot.setBreed(false);
        parrot.setAgeLock(true);
        parrot.setAI(false);

        parrot.addScoreboardTag(invulnerableTag);

        entities.add(parrot);

        startFollowTask(instance, owner);
    }
}
