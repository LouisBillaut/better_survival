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

public class Bee extends Pet {

    private void setFields() {
        price = 10000;
        ItemStack beeEgg = new ItemStack(Material.BEE_SPAWN_EGG);
        ItemMeta itemMeta = beeEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Bee");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        beeEgg.setItemMeta(itemMeta);
        item = new ItemStack(beeEgg);
        name = "bee";
    }
    public Bee() {
        super();
        setFields();
    }
    public Bee(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Bee bee = (org.bukkit.entity.Bee) owner.getWorld().spawnEntity(location, EntityType.BEE);
        bee.setInvulnerable(true);
        bee.setGravity(false);
        bee.setSilent(true);
        bee.setCanPickupItems(false);
        bee.setBreed(false);
        bee.setAgeLock(true);
        bee.setAI(false);
        if (customName != null) {
            bee.setCustomName(customName);
            bee.setCustomNameVisible(true);
        }

        bee.addScoreboardTag(invulnerableTag);

        entities.add(bee);

        startFollowTask(instance, owner);
    }
}
