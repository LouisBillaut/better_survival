package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Rabbit extends Pet {
    public Rabbit(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Rabbit rabbit = (org.bukkit.entity.Rabbit) owner.getWorld().spawnEntity(location, EntityType.RABBIT);
        rabbit.setInvulnerable(true);
        rabbit.setGravity(false);
        rabbit.setSilent(true);
        rabbit.setBreed(false);
        rabbit.setAgeLock(true);
        rabbit.setAI(false);

        rabbit.addScoreboardTag(invulnerableTag);

        entities.add(rabbit);

        startFollowTask();
    }
}
