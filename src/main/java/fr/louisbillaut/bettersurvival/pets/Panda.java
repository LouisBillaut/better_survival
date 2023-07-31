package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Panda extends Pet {
    public Panda(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Panda panda = (org.bukkit.entity.Panda) owner.getWorld().spawnEntity(location, EntityType.PANDA);
        panda.setBaby();
        panda.setInvulnerable(true);
        panda.setGravity(false);
        panda.setSilent(true);
        panda.setBreed(false);
        panda.setAgeLock(true);
        panda.setAI(false);

        panda.addScoreboardTag(invulnerableTag);

        entities.add(panda);

        startFollowTask();
    }
}
