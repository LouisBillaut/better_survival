package fr.louisbillaut.bettersurvival.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Detector {
    public static boolean isInZone(Location location, Location location1, Location location2, int height) {
        if (!location.getWorld().equals(location2.getWorld())) {
            return false;
        }

        double minX = Math.floor(Math.min(location1.getX(), location2.getX()));
        double minZ = Math.floor(Math.min(location1.getZ(), location2.getZ()));
        double minY = Math.floor(Math.min(location1.getY(), location2.getY()));
        double maxY = minY + height;

        boolean isWithinHorizontalBounds = (location.getX() >= minX && location.getX() <= Math.max(location1.getX(), location2.getX()));
        boolean isWithinVerticalBounds = (location.getZ() >= minZ && location.getZ() <= Math.max(location1.getZ(), location2.getZ()));
        boolean isWithinHeight = (height==-1) || (location.getY() >= minY && location.getY() <= maxY);

        return isWithinHorizontalBounds && isWithinVerticalBounds && isWithinHeight;
    }

    public static boolean isInZoneWithRadius(Location location, Location location1, Location location2, int height, int radius) {
        if (!location.getWorld().equals(location2.getWorld())) {
            return false;
        }

        double minX = Math.floor(Math.min(location1.getX(), location2.getX())) - radius;
        double minZ = Math.floor(Math.min(location1.getZ(), location2.getZ())) - radius;
        double minY = Math.floor(Math.min(location1.getY(), location2.getY())) - radius;
        double maxZ = Math.max(location1.getZ(), location2.getZ()) + radius;
        double maxX = Math.max(location1.getX(), location2.getX()) + radius;
        double maxY = minY + height + (radius * 2);

        boolean isWithinHorizontalBounds = (location.getX() >= minX && location.getX() <= maxX);
        boolean isWithinVerticalBounds = (location.getZ() >= minZ && location.getZ() <= maxZ);
        boolean isWithinHeight = (height==-1) || (location.getY() >= minY && location.getY() <= maxY);

        return isWithinHorizontalBounds && isWithinVerticalBounds && isWithinHeight;
    }

    public static boolean isInWhiteList(Player player, ArrayList<String> whiteList) {
        String playerName = player.getName();
        return whiteList.contains(playerName);
    }
}
