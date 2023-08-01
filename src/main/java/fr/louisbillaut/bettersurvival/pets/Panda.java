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

public class Panda extends Pet {
    public Panda(Main instance, Player owner) {
        super(instance, owner);
        price = 10000;
        ItemStack allayEgg = new ItemStack(Material.PANDA_SPAWN_EGG);
        ItemMeta itemMeta = allayEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Panda");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        allayEgg.setItemMeta(itemMeta);
        item = new ItemStack(allayEgg);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Panda panda = (org.bukkit.entity.Panda) owner.getWorld().spawnEntity(location, EntityType.PANDA);
        panda.setBaby();
        panda.setInvulnerable(true);
        panda.setGravity(false);
        panda.setSilent(true);
        panda.setBreed(false);
        panda.setAgeLock(true);
        panda.setAI(false);

        panda.addScoreboardTag(invulnerableTag);

        entities.add(panda);

        startFollowTask();
    }
}
