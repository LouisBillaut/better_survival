package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Fox extends Pet {
    public Fox(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Fox fox = (org.bukkit.entity.Fox) owner.getWorld().spawnEntity(location, EntityType.FOX);
        fox.setBaby();
        fox.setInvulnerable(true);
        fox.setGravity(false);
        fox.setSilent(true);
        fox.setBreed(false);
        fox.setAgeLock(true);
        fox.setAI(false);

        fox.addScoreboardTag(invulnerableTag);

        entities.add(fox);

        startFollowTask();
    }
}
