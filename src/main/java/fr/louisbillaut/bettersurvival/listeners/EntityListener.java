package fr.louisbillaut.bettersurvival.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            Villager villager = (Villager) event.getEntity();
            if (villager.isCustomNameVisible() && !villager.getCustomName().equals("")) {
                event.setCancelled(true);
            }
        }
    }
}
