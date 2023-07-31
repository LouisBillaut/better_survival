package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Axolotl extends Pet {
    public Axolotl(Main instance, Player owner) {
        super(instance, owner);
    }
    @Override
    public void spawn() {
        Location location = owner.getLocation();
        org.bukkit.entity.Axolotl axolotl = (org.bukkit.entity.Axolotl) owner.getWorld().spawnEntity(location, EntityType.AXOLOTL);
        axolotl.setInvulnerable(true);
        axolotl.setGravity(false);
        axolotl.setSilent(true);
        axolotl.setCanPickupItems(false);
        axolotl.setPlayingDead(false);
        axolotl.setBreed(false);
        axolotl.setAgeLock(true);
        axolotl.setAI(false);
        axolotl.setVariant(org.bukkit.entity.Axolotl.Variant.LUCY);

        axolotl.addScoreboardTag(invulnerableTag);

        entities.add(axolotl);

        startFollowTask();
    }
}
