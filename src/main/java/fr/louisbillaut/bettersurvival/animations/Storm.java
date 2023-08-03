package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;

public class Storm extends Animation {
    private final Random random = new Random();
    private ArmorStand armorStand;
    public Storm() {
        super();
    }
    public Storm(Main instance) {
        super(instance);
    }

    private void spawnParticle(Player player) {
        Location playerLocation = player.getLocation();
        Vector eyeDirection = playerLocation.getDirection().normalize().multiply(-0.5);
        Location armorStandLocation = playerLocation.clone().add(0, 2, 0).add(eyeDirection);

        armorStand.teleport(armorStandLocation);

        player.getWorld().spawnParticle(Particle.FALLING_DRIPSTONE_WATER, playerLocation.clone().add(0, 2.5, 0).add(eyeDirection), 1, 0.2, 0, 0.2, 1);
    }

    @Override
    public void startAnimation(Player player) {
        Location playerLocation = player.getLocation();
        Location armorStandLocation = playerLocation.clone().add(0, 2, 0);
        armorStand = (ArmorStand) player.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setHelmet(Head.getCustomHead(Head.rainCloud));
        animation = new BukkitRunnable(){

            @Override
            public void run() {
                spawnParticle(player);
            }
        }.runTaskTimerAsynchronously(instance, 0, 1);
    }

    @Override
    public void stopAnimation() {
        animation.cancel();
        armorStand.remove();
    }
}
