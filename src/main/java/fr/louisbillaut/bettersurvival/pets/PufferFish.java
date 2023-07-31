package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PufferFish extends Pet {
    public PufferFish(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.PufferFish pufferFish = (org.bukkit.entity.PufferFish) owner.getWorld().spawnEntity(location, EntityType.PUFFERFISH);
        pufferFish.setInvulnerable(true);
        pufferFish.setGravity(false);
        pufferFish.setSilent(true);
        pufferFish.setAI(false);
        pufferFish.setPuffState(3);

        pufferFish.addScoreboardTag(invulnerableTag);

        entities.add(pufferFish);

        startFollowTask();
    }
}
