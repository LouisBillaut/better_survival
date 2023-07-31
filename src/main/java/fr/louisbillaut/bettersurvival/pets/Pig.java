package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Pig extends Pet {
    public Pig(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Pig pig = (org.bukkit.entity.Pig) owner.getWorld().spawnEntity(location, EntityType.PIG);
        pig.setBaby();
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setSilent(true);
        pig.setBreed(false);
        pig.setAgeLock(true);
        pig.setAI(false);

        pig.addScoreboardTag(invulnerableTag);

        entities.add(pig);

        startFollowTask();
    }
}
