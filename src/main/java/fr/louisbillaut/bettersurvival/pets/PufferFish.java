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

public class PufferFish extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack egg = new ItemStack(Material.PUFFERFISH_SPAWN_EGG);
        ItemMeta itemMeta = egg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Puffer fish");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        egg.setItemMeta(itemMeta);
        item = new ItemStack(egg);
        name = "pufferFish";
    }
    public PufferFish() {
        setFields();
    }
    public PufferFish(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.PufferFish pufferFish = (org.bukkit.entity.PufferFish) owner.getWorld().spawnEntity(location, EntityType.PUFFERFISH);
        pufferFish.setInvulnerable(true);
        pufferFish.setGravity(false);
        pufferFish.setSilent(true);
        pufferFish.setAI(false);
        pufferFish.setPuffState(3);
        if (customName != null) {
            pufferFish.setCustomName(customName);
            pufferFish.setCustomNameVisible(true);
        }

        pufferFish.addScoreboardTag(invulnerableTag);

        entities.add(pufferFish);

        startFollowTask(instance, owner);
    }
}
