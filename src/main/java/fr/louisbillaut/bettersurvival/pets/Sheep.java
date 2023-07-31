package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Sheep extends Pet {
    public Sheep(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Sheep sheep = (org.bukkit.entity.Sheep) owner.getWorld().spawnEntity(location, EntityType.SHEEP);
        sheep.setBaby();
        sheep.setInvulnerable(true);
        sheep.setGravity(false);
        sheep.setSilent(true);
        sheep.setBreed(false);
        sheep.setAgeLock(true);
        sheep.setAI(false);

        sheep.addScoreboardTag(invulnerableTag);

        entities.add(sheep);

        startFollowTask();
    }
}
