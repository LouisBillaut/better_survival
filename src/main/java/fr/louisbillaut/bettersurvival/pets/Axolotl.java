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

public class Axolotl extends Pet {

    public void setFields() {
        price = 15000;
        ItemStack axolotlEgg = new ItemStack(Material.AXOLOTL_SPAWN_EGG);
        ItemMeta itemMeta = axolotlEgg.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Axolotl");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        itemMeta.setLore(lore);
        axolotlEgg.setItemMeta(itemMeta);
        item = new ItemStack(axolotlEgg);
        name = "axolotl";
    }
    public Axolotl() {
        super();
        setFields();
    }
    public Axolotl(Main instance, Player owner) {
        super(instance, owner);
        setFields();
    }
    @Override
    public void spawn(Player owner) {
        Location location = owner.getLocation();
        org.bukkit.entity.Axolotl axolotl = (org.bukkit.entity.Axolotl) owner.getWorld().spawnEntity(location, EntityType.AXOLOTL);
        axolotl.setInvulnerable(true);
        axolotl.setGravity(false);
        axolotl.setSilent(true);
        axolotl.setCanPickupItems(false);
        axolotl.setPlayingDead(false);
        axolotl.setBreed(false);
        axolotl.setAgeLock(true);
        axolotl.setAI(false);

        axolotl.addScoreboardTag(invulnerableTag);

        entities.add(axolotl);

        startFollowTask(owner);
    }
}
