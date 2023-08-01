package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Allay extends Pet {

    private void setFields() {
        price = 10000;
        ItemStack allayEgg = new ItemStack(Material.BLAZE_SPAWN_EGG);
        ItemMeta itemMeta = allayEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Allay");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        allayEgg.setItemMeta(itemMeta);
        item = new ItemStack(allayEgg);
    }
    public Allay() {
        super();
        setFields();
    }
    public Allay(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Allay allay = (org.bukkit.entity.Allay) owner.getWorld().spawnEntity(location, EntityType.ALLAY);
        allay.setCanDuplicate(false);
        allay.setInvulnerable(true);
        allay.setGravity(false);
        allay.setSilent(true);
        allay.setCanPickupItems(false);
        allay.setAI(false);

        allay.addScoreboardTag(invulnerableTag);

        entities.add(allay);

        startFollowTask();
    }
}
