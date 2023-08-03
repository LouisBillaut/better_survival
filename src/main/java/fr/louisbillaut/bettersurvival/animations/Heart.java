package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Heart extends Animation {
    private final Random random = new Random();
    public Heart() {
        super();
    }
    public Heart(Main instance) {
        super(instance);
    }

    private void spawnParticle(Player player) {
        Location loc = getPlayerLocationBehind(player);
        player.getWorld().spawnParticle(org.bukkit.Particle.HEART, loc, 1, 0.2, 0, 0.2, 0);
    }

    private Location getPlayerLocationBehind(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();
        double distanceBehind = -0.4;
        double offsetX = direction.getX() * distanceBehind;
        double offsetZ = direction.getZ() * distanceBehind;

        return playerLocation.add(offsetX, 2.3, offsetZ);
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
