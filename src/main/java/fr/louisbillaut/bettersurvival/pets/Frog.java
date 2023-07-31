package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Frog extends Pet {
    public Frog(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Frog frog = (org.bukkit.entity.Frog) owner.getWorld().spawnEntity(location, EntityType.FROG);
        frog.setBaby();
        frog.setInvulnerable(true);
        frog.setGravity(false);
        frog.setSilent(true);
        frog.setBreed(false);
        frog.setAgeLock(true);
        frog.setAI(false);

        frog.addScoreboardTag(invulnerableTag);

        entities.add(frog);

        startFollowTask();
    }
}
