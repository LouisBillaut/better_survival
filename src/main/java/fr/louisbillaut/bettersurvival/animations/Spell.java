package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spell extends Animation {
    private void setFields() {
        price = 12000;
        var head = Head.getCustomHead(Head.spell);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Spell");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        meta.setLore(lore);
        head.setItemMeta(meta);

        item = head;
        name = "spell";
    }
    public Spell() {
        super();
        setFields();
    }
    public Spell(Main instance) {
        super(instance);
        setFields();
    }

    private void spawnParticle(Player player) {
        Location loc = getPlayerLocationBehind(player);
        player.getWorld().spawnParticle(Particle.SPELL, loc, 1, 0.2, 0, 0.2, 0);
        player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0.2, 0, 0.2, 0);
    }

    private Location getPlayerLocationBehind(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();
        double distanceBehind = -0.4;
        double offsetX = direction.getX() * distanceBehind;
        double offsetZ = direction.getZ() * distanceBehind;

        return playerLocation.add(offsetX, 2, offsetZ);
    }

    @Override
    public void startAnimation(Main instance, Player player) {
        animation = new BukkitRunnable(){

            @Override
            public void run() {
                spawnParticle(player);
            }
        }.runTaskTimerAsynchronously(instance, 0, 3);
    }
}
