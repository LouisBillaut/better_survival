package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class EasterEgg {
    private Location daveLocation;
    
    public EasterEgg() {
        
    }

    public void setDaveLocation(Location daveLocation) {
        this.daveLocation = daveLocation;
    }

    public Location getDaveLocation() {
        return daveLocation;
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("daveLocation", daveLocation);
    }

    public void loadFromConfig(Main instance, Game game, ConfigurationSection config) {
        if (config.contains("daveLocation")) {
            daveLocation = config.getLocation("daveLocation");
        }
    }

    public static ItemStack getLavaGhostHeadItemEaster(Main instance) {
        var head = Head.getCustomHead(Head.ghostLava);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Easter");
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack getLavaGhostHeadItem() {
        var head = Head.getCustomHead(Head.ghostLava);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lava Ghost");
        head.setItemMeta(meta);

        return head;
    }
}
