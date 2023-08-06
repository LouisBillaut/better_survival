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

public class Frog extends Pet {
    private void setFields() {
        price = 10000;
        ItemStack frogEgg = new ItemStack(Material.FROG_SPAWN_EGG);
        ItemMeta itemMeta = frogEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Frog");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        frogEgg.setItemMeta(itemMeta);
        item = new ItemStack(frogEgg);
        name = "frog";
    }
    public Frog() {
        setFields();
    }
    public Frog(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Main instance, Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Frog frog = (org.bukkit.entity.Frog) owner.getWorld().spawnEntity(location, EntityType.FROG);
        frog.setBaby();
        frog.setInvulnerable(true);
        frog.setGravity(false);
        frog.setSilent(true);
        frog.setBreed(false);
        frog.setAgeLock(true);
        frog.setAI(false);

        frog.addScoreboardTag(invulnerableTag);

        entities.add(frog);

        startFollowTask(instance, owner);
    }
}
