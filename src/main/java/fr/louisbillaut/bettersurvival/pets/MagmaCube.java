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

public class MagmaCube extends Pet {
    public MagmaCube(Main instance, Player owner) {
        super(instance, owner);
        price = 10000;
        ItemStack allayEgg = new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
        ItemMeta itemMeta = allayEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Magma cube");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        allayEgg.setItemMeta(itemMeta);
        item = new ItemStack(allayEgg);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.MagmaCube magmaCube = (org.bukkit.entity.MagmaCube) owner.getWorld().spawnEntity(location, EntityType.MAGMA_CUBE);
        magmaCube.setInvulnerable(true);
        magmaCube.setGravity(false);
        magmaCube.setSilent(true);
        magmaCube.setSize(1);
        magmaCube.setAI(false);

        magmaCube.addScoreboardTag(invulnerableTag);

        entities.add(magmaCube);

        startFollowTask();
    }
}
