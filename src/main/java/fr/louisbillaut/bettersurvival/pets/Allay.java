package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class Allay extends Pet {
    public Allay(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Allay allay = (org.bukkit.entity.Allay) owner.getWorld().spawnEntity(location, EntityType.ALLAY);
        allay.setCanDuplicate(false);
        allay.setInvulnerable(true);
        allay.setGravity(false);
        allay.setSilent(true);
        allay.setCanPickupItems(false);
        allay.setAI(false);

        allay.addScoreboardTag(invulnerableTag);

        entities.add(allay);

        startFollowTask();
    }
}
