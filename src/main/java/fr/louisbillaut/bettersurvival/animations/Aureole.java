package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Aureole extends Animation {
    private final Random random = new Random();
    private final int numParticles = 30;
    private void setFields() {
        price = 15000;
        var head = Head.getCustomHead(Head.angel);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Aureole");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "price: " + price + " bsBucks");
        meta.setLore(lore);
        head.setItemMeta(meta);

        item = head;
    }
    public Aureole() {
        super();
        setFields();
    }
    public Aureole(Main instance) {
        super(instance);
        setFields();
    }

    private void spawnParticle(Player player) {
        double angleIncrement = (2 * Math.PI) / numParticles;
        double radius = 0.4;

        for (int i = 0; i < numParticles; i++) {
            double angle = angleIncrement * i;
            Location loc = getPlayerLocation(player, angle, radius);
            Color color = Color.YELLOW;
            Particle.DustOptions options = new Particle.DustTransition(color, color, 1);
            player.getWorld().spawnParticle(org.bukkit.Particle.DUST_COLOR_TRANSITION, loc, 1, options);
        }
    }

    private Location getPlayerLocation(Player player, double angle, double radius) {
        Location playerLocation = player.getLocation();
        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        return playerLocation.add(offsetX, 2.2, offsetZ);
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
    public void startAnimation(Player player) {
        animation = new BukkitRunnable(){

            @Override
            public void run() {
                spawnParticle(player);
            }
        }.runTaskTimerAsynchronously(instance, 0, 5);
    }
}
