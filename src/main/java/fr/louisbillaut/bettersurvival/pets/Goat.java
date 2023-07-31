package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Goat extends Pet {
    public Goat(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Goat goat = (org.bukkit.entity.Goat) owner.getWorld().spawnEntity(location, EntityType.GOAT);
        goat.setBaby();
        goat.setInvulnerable(true);
        goat.setGravity(false);
        goat.setSilent(true);
        goat.setBreed(false);
        goat.setAgeLock(true);
        goat.setAI(false);

        goat.addScoreboardTag(invulnerableTag);

        entities.add(goat);

        startFollowTask();
    }
}
