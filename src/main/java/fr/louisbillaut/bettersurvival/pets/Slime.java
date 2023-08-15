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

public class Slime extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.SLIME_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Slime");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
        name = "slime";
    }
    public Slime() {
        setFields();
    }
    public Slime(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Slime slime = (org.bukkit.entity.Slime) owner.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setInvulnerable(true);
        slime.setGravity(false);
        slime.setSilent(true);
        slime.setSize(1);
        slime.setAI(false);
        if (customName != null) {
            slime.setCustomName(customName);
            slime.setCustomNameVisible(true);
        }

        slime.addScoreboardTag(invulnerableTag);

        entities.add(slime);

        startFollowTask(instance, owner);
    }
}
