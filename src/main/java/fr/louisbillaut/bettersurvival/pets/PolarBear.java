package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PolarBear extends Pet {
    public PolarBear(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.PolarBear polarBear = (org.bukkit.entity.PolarBear) owner.getWorld().spawnEntity(location, EntityType.POLAR_BEAR);
        polarBear.setBaby();
        polarBear.setInvulnerable(true);
        polarBear.setGravity(false);
        polarBear.setSilent(true);
        polarBear.setBreed(false);
        polarBear.setAgeLock(true);
        polarBear.setAI(false);

        polarBear.addScoreboardTag(invulnerableTag);

        entities.add(polarBear);

        startFollowTask();
    }
}
