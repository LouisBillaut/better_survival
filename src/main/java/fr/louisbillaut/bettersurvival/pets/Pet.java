package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public abstract class Pet {
    public static final String invulnerableTag = "invulnerable";
    protected final Player owner;
    protected final Main instance;
    protected List<LivingEntity> entities;
    protected boolean isSecret = false;
    protected ItemStack item;
    protected int price;
    protected BukkitTask animation;
    protected BukkitTask followTask;

    public Pet(Main instance, Player owner) {
        this.owner = owner;
        this.instance = instance;
        this.entities = new ArrayList<>();
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public abstract void spawn();

    public void despawn() {
        for (LivingEntity entity : entities) {
            if (entity != null && !entity.isDead()) {
                entity.remove();
            }
        }
        if (animation != null) {
            animation.cancel();
        }
        if (followTask != null) {
            followTask.cancel();
        }
        entities.clear();
    }
    protected void startFollowTask() {
        followTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entities.isEmpty() || owner == null || owner.isDead()) {
                    this.cancel();
                    return;
                }


                Location ownerLocation = owner.getLocation();
                double playerYaw = Math.toRadians(ownerLocation.getYaw());

                double radius = 1;
                double angleIncrement = (2 * Math.PI) / entities.size();

                double distanceBehind = -0.5;

                for (int i = 0; i < entities.size(); i++) {
                    LivingEntity entity = entities.get(i);
                    double angle = playerYaw + (i * angleIncrement);

                    double x = radius * Math.cos(angle) - distanceBehind * Math.sin(playerYaw);
                    double y = 1.6;
                    double z = radius * Math.sin(angle) + distanceBehind * Math.cos(playerYaw);

                    Location targetLocation = ownerLocation.clone().add(x, y, z);

                    entity.teleport(targetLocation);

                    entity.setVelocity(ownerLocation.getDirection());
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 1);
    }

    public void handleSneakToggle(PlayerToggleSneakEvent event) {
        if (event.getPlayer().equals(owner)) {
            if (event.isSneaking()) {
                despawn();
            } else {
                spawn();
            }
        }
    }
}
