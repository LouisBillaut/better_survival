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

public class Cat extends Pet {

    private void setFields() {
        price = 15000;
        ItemStack catEgg = new ItemStack(Material.CAT_SPAWN_EGG);
        ItemMeta itemMeta = catEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Cat");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        catEgg.setItemMeta(itemMeta);
        item = new ItemStack(catEgg);
    }
    public Cat() {
        setFields();
    }
    public Cat(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Cat cat = (org.bukkit.entity.Cat) owner.getWorld().spawnEntity(location, EntityType.CAT);
        cat.setBaby();
        cat.setInvulnerable(true);
        cat.setGravity(false);
        cat.setSilent(true);
        cat.setBreed(false);
        cat.setAI(false);
        cat.setAgeLock(true);

        cat.addScoreboardTag(invulnerableTag);

        entities.add(cat);

        startFollowTask(owner);
    }
}
