package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Cat extends Pet {
    public Cat(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Cat cat = (org.bukkit.entity.Cat) owner.getWorld().spawnEntity(location, EntityType.CAT);
        cat.setBaby();
        cat.setInvulnerable(true);
        cat.setGravity(false);
        cat.setSilent(true);
        cat.setBreed(false);
        cat.setAI(false);
        cat.setAgeLock(true);

        cat.addScoreboardTag(invulnerableTag);

        entities.add(cat);

        startFollowTask();
    }
}
