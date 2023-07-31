package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Parrot extends Pet {
    public Parrot(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Parrot parrot = (org.bukkit.entity.Parrot) owner.getWorld().spawnEntity(location, EntityType.PARROT);
        parrot.setBaby();
        parrot.setInvulnerable(true);
        parrot.setGravity(false);
        parrot.setSilent(true);
        parrot.setBreed(false);
        parrot.setAgeLock(true);
        parrot.setAI(false);

        parrot.addScoreboardTag(invulnerableTag);

        entities.add(parrot);

        startFollowTask();
    }
}
