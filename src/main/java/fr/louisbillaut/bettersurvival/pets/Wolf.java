package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Wolf extends Pet {
    public Wolf(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) owner.getWorld().spawnEntity(location, EntityType.WOLF);
        wolf.setBaby();
        wolf.setInvulnerable(true);
        wolf.setGravity(false);
        wolf.setSilent(true);
        wolf.setBreed(false);
        wolf.setAgeLock(true);
        wolf.setAI(false);

        wolf.addScoreboardTag(invulnerableTag);

        entities.add(wolf);

        startFollowTask();
    }
}
