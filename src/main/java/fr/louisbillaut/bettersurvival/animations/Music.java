package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Music extends Animation {
    private final Random random = new Random();
    public Music() {
        super();
    }
    public Music(Main instance) {
        super(instance);
    }

    private void spawnParticle(Player player) {
        Location loc = getPlayerLocationBehind(player);
        player.getWorld().spawnParticle(Particle.NOTE, loc, 0, getRandomColor(), 0, 0, 1);
    }

    private Location getPlayerLocationBehind(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();
        double distanceBehind = -0.4;
        double offsetX = direction.getX() * distanceBehind;
        double offsetZ = direction.getZ() * distanceBehind;

        return playerLocation.add(offsetX, 2.3, offsetZ);
    }

    private double getRandomColor() {
        return random.nextInt(24) / 24D;
    }

    @Override
    public void startAnimation(Player player) {
        animation = new BukkitRunnable(){

            @Override
            public void run() {
                spawnParticle(player);
            }
        }.runTaskTimerAsynchronously(instance, 0, 10);
    }
}
