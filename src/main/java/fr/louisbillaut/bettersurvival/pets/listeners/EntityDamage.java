package fr.louisbillaut.bettersurvival.pets.listeners;

import fr.louisbillaut.bettersurvival.pets.Pet;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (entity.getScoreboardTags().contains(Pet.invulnerableTag)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (event.getEntity().getScoreboardTags().contains(Pet.invulnerableTag)) {
            event.setCancelled(true);
        }
    }
}
