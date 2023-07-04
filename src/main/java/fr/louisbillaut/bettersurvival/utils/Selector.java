package fr.louisbillaut.bettersurvival.utils;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Selector {
    private static final String arrowDownHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxMmQ0NWIxYzc4Y2MyMjQ1MjcyM2VlNjZiYTJkMTU3NzdjYzI4ODU2OGQ2YzFiNjJhNTQ1YjI5YzcxODcifX19";

    public static void appearArmorStand(Main instance, Game game, Player player) {
        var armorStands = game.armorStands;
        var rotationTasks = game.armorStandsRotationTasks;

        Block targetBlock = player.getTargetBlock(null, 100);
        if (targetBlock.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "No block found !");
        }

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(targetBlock.getLocation().add(0.5, 0.6, 0.5), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);

        ItemStack skull = Head.getCustomHead(arrowDownHead);

        armorStand.setHelmet(skull);

        if (!armorStands.containsKey(player.getUniqueId())) {
            armorStands.put(player.getUniqueId(), new ArrayList<>(List.of(armorStand)));
        } else {
            armorStands.get(player.getUniqueId()).add(armorStand);
        }
        player.sendMessage(ChatColor.GREEN + "Armor Stand créé !");

        BukkitRunnable rotationTask = startRotationTask(instance, armorStand);
        if (!rotationTasks.containsKey(player.getUniqueId())) {
            rotationTasks.put(player.getUniqueId(), new ArrayList<>(List.of(rotationTask)));
        } else {
            rotationTasks.get(player.getUniqueId()).add(rotationTask);
        }
    }

    private static BukkitRunnable startRotationTask(Main instance, ArmorStand armorStand) {
        BukkitRunnable rotationTask = new BukkitRunnable() {
            //double angle = 0;
            //double maxHeightOffset = 0.2;
            @Override
            public void run() {
                EulerAngle currentRotation = armorStand.getHeadPose();
                EulerAngle newRotation = currentRotation.add(0, 0.15, 0);

                armorStand.setHeadPose(newRotation);

                /*double heightOffset = Math.sin(angle) * maxHeightOffset;
                double previousHeightOffset = Math.sin(angle - 0.1) * maxHeightOffset;
                double newY = armorStand.getLocation().getY() - previousHeightOffset + heightOffset;

                Location newLocation = new Location(armorStand.getWorld(), armorStand.getLocation().getX(), newY, armorStand.getLocation().getZ());
                armorStand.teleport(newLocation);

                angle += 0.1;*/
            }
        };

        rotationTask.runTaskTimerAsynchronously(instance, 0, 1);
        return rotationTask;
    }
}
