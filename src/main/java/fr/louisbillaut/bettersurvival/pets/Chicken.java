package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Chicken extends Pet {
    public Chicken(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Chicken chicken = (org.bukkit.entity.Chicken) owner.getWorld().spawnEntity(location, EntityType.CHICKEN);
        chicken.setInvulnerable(true);
        chicken.setGravity(false);
        chicken.setSilent(true);
        chicken.setBreed(false);
        chicken.setAgeLock(true);
        chicken.setAI(false);

        chicken.addScoreboardTag(invulnerableTag);

        entities.add(chicken);

        startFollowTask();
    }
}
