package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.ActionBar;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Compass {
    BukkitTask runnable;
    List<String> targets = new ArrayList<>();
    List<Location> targetsLocations = new ArrayList<>();

    public Compass() {}

    private String getArrow(double angle) {
        String[] arrows = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

        angle = (angle + 360) % 360;

        int index = (int) Math.round(angle / 45.0) % 8;

        return arrows[index];
    }

    public BukkitTask getCompassTask(Main instance, org.bukkit.entity.Player player) {
        return new BukkitRunnable(){

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                String actionBar = "";
                for (var i= 0; i < targets.size() && i < targetsLocations.size(); i++) {
                    if (targets.get(i) == null || targetsLocations.get(i) == null) continue;
                    Location playerLocation = player.getLocation();
                    int distance = (int) playerLocation.distance(targetsLocations.get(i));
                    Vector playerDirection = player.getEyeLocation().getDirection().normalize();

                    Vector targetDirection = targetsLocations.get(i).toVector().subtract(playerLocation.toVector()).normalize();

                    double angle = Math.toDegrees(Math.atan2(targetDirection.getZ(), targetDirection.getX()) - Math.atan2(playerDirection.getZ(), playerDirection.getX()));

                    angle = (angle + 360) % 360;

                    var color = ChatColor.GREEN;
                    if (distance > 500) {
                        color = ChatColor.RED;
                    }
                    if (distance < 500 && distance > 100) {
                        color = ChatColor.GOLD;
                    }

                    actionBar += color + targets.get(i) + " (" + distance + ") " + ChatColor.BOLD + getArrow(angle) + " ";
                }
                ActionBar.sendActionBar(player, actionBar);

            }
        }.runTaskTimerAsynchronously(instance, 0L, 1);
    }

    public void addTarget(Main instance, Player player, Location targetLocation, String target) {
        if (targets.size() == 0) {
            runnable = getCompassTask(instance, player);
        }
        if (targets.size() < 3) {
            targets.add(target);
        } else{
            targets.add(target);
            for(var i = 2; i >=0; i--) {
                targets.set(i + 1, targets.get(i));
            }
            targets.remove(3);
            targets.set(0, target);
        }

        if (targetsLocations.size() < 3) {
            targetsLocations.add(targetLocation);
        } else{
            targetsLocations.add(targetLocation);
            for(var i = 2; i >=0; i--) {
                targetsLocations.set(i + 1, targetsLocations.get(i));
            }
            targetsLocations.remove(3);
            targetsLocations.set(0, targetLocation);
        }
    }

    public void clear(Player player) {
        runnable.cancel();
        targets = new ArrayList<>();
        targetsLocations = new ArrayList<>();
        ActionBar.sendActionBar(player, "");
    }
}
