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

public class Fox extends Pet {

    private void setFields() {
        price = 10000;
        ItemStack foxEgg = new ItemStack(Material.FOX_SPAWN_EGG);
        ItemMeta itemMeta = foxEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Fox");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        foxEgg.setItemMeta(itemMeta);
        item = new ItemStack(foxEgg);
        name = "fox";
    }
    public Fox() {
        super();
        setFields();
    }
    public Fox(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Fox fox = (org.bukkit.entity.Fox) owner.getWorld().spawnEntity(location, EntityType.FOX);
        fox.setBaby();
        fox.setInvulnerable(true);
        fox.setGravity(false);
        fox.setSilent(true);
        fox.setBreed(false);
        fox.setAgeLock(true);
        fox.setAI(false);

        if (customName != null) {
            fox.setCustomName(customName);
            fox.setCustomNameVisible(true);
        }

        fox.addScoreboardTag(invulnerableTag);

        entities.add(fox);

        startFollowTask(instance, owner);
    }
}
