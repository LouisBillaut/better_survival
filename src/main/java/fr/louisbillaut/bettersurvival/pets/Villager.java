package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Villager extends Pet {
    public Villager(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Villager villager = (org.bukkit.entity.Villager) owner.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setBaby();
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setSilent(true);
        villager.setBreed(false);
        villager.setAgeLock(true);
        villager.setAI(false);

        villager.addScoreboardTag(invulnerableTag);

        entities.add(villager);

        startFollowTask();
    }
}
