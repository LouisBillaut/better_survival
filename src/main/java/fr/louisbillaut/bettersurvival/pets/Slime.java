package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Slime extends Pet {
    public Slime(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Slime slime = (org.bukkit.entity.Slime) owner.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setInvulnerable(true);
        slime.setGravity(false);
        slime.setSilent(true);
        slime.setSize(1);
        slime.setAI(false);

        slime.addScoreboardTag(invulnerableTag);

        entities.add(slime);

        startFollowTask();
    }
}
