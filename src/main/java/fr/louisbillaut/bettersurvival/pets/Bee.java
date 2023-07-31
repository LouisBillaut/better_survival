package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Bee extends Pet {
    public Bee(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Bee bee = (org.bukkit.entity.Bee) owner.getWorld().spawnEntity(location, EntityType.BEE);
        bee.setInvulnerable(true);
        bee.setGravity(false);
        bee.setSilent(true);
        bee.setCanPickupItems(false);
        bee.setBreed(false);
        bee.setAgeLock(true);
        bee.setAI(false);

        bee.addScoreboardTag(invulnerableTag);

        entities.add(bee);

        startFollowTask();
    }
}
