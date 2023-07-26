package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand implements CommandExecutor {
    private Main instance;
    private Game game;

    private static final int hostileMobsRange = 15;

    public SpawnCommand(Main instance, Game game) {
        this.instance = instance;
        this.game =game;
    }
    private boolean hasHostileMobsNearby(Location location, double radius) {
        World world = location.getWorld();
        for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof Player) continue;
            if (entity.isDead() || !entity.isValid()) continue;
            if (entity.isInsideVehicle()) continue;

            switch (entity.getType()) {
                case CREEPER:
                case ZOMBIE:
                case SKELETON:
                case WITHER_SKELETON:
                case SPIDER:
                case BLAZE:
                case SILVERFISH:
                case WARDEN:
                case RAVAGER:
                case ELDER_GUARDIAN:
                case GUARDIAN:
                case SLIME:
                case STRAY:
                case ENDERMITE:
                case PHANTOM:
                case WITCH:
                case PILLAGER:
                case DROWNED:
                case GHAST:
                    return true;
            }
        }
        return false;
    }

    private void startTeleportation(Player player) {
        player.setMetadata("teleportingSpawn", new FixedMetadataValue(instance, true));
        player.sendMessage(ChatColor.GREEN + "You will be teleported to spawn in 5 seconds. Don't move!");

        fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
        if (playerIG == null) return;
        playerIG.setTeleportRunnable(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.hasMetadata("teleportingSpawn")) return;

                        teleportToSpawn(player);
                    }
                }.runTaskLater(instance, 100)
        );
    }


    private void teleportToSpawn(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have been teleported to spawn!");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
        World overworld = Bukkit.getWorld("world");
        Location spawnLocation = overworld.getSpawnLocation();
        player.teleport(spawnLocation);
        player.removeMetadata("teleportingSpawn", instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("spawn")) {
            Player player = (Player) sender;

            if (hasHostileMobsNearby(player.getLocation(), hostileMobsRange)) {
                player.sendMessage(ChatColor.RED + "There are hostile mobs nearby. You cannot use this command.");
                return true;
            }

            startTeleportation(player);
        }

        return true;
    }
}
