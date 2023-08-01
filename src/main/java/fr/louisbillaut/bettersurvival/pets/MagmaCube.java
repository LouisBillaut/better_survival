package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MagmaCube extends Pet {
    public MagmaCube(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.MagmaCube magmaCube = (org.bukkit.entity.MagmaCube) owner.getWorld().spawnEntity(location, EntityType.MAGMA_CUBE);
        magmaCube.setInvulnerable(true);
        magmaCube.setGravity(false);
        magmaCube.setSilent(true);
        magmaCube.setSize(1);
        magmaCube.setAI(false);

        magmaCube.addScoreboardTag(invulnerableTag);

        entities.add(magmaCube);

        startFollowTask();
    }
}
