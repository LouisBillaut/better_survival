package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Cow extends Pet {
    public Cow(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Cow cow = (org.bukkit.entity.Cow) owner.getWorld().spawnEntity(location, EntityType.COW);
        cow.setBaby();
        cow.setInvulnerable(true);
        cow.setGravity(false);
        cow.setSilent(true);
        cow.setBreed(false);
        cow.setAgeLock(true);
        cow.setAI(false);

        cow.addScoreboardTag(invulnerableTag);

        entities.add(cow);

        startFollowTask();
    }
}
