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

public class Villager extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Villager");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
    }
    public Villager() {
        setFields();
    }
    public Villager(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Villager villager = (org.bukkit.entity.Villager) owner.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setBaby();
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setSilent(true);
        villager.setBreed(false);
        villager.setAgeLock(true);
        villager.setAI(false);

        villager.addScoreboardTag(invulnerableTag);

        entities.add(villager);

        startFollowTask(owner);
    }
}
