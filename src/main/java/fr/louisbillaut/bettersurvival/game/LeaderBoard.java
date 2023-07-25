package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LeaderBoard {
    private Location number3PNJ;
    private Location number2PNJ;
    private Location number1PNJ;
    private Location bsBucksLeaderBoard;

    public LeaderBoard() {}

    public Location getNumber3PNJ() {
        return number3PNJ;
    }

    public void setNumber3PNJ(Location number3PNJ) {
        this.number3PNJ = number3PNJ;
    }

    public Location getNumber2PNJ() {
        return number2PNJ;
    }

    public void setNumber2PNJ(Location number2PNJ) {
        this.number2PNJ = number2PNJ;
    }

    public Location getNumber1PNJ() {
        return number1PNJ;
    }

    public void setNumber1PNJ(Location number1PNJ) {
        this.number1PNJ = number1PNJ;
    }

    public Location getBsBucksLeaderBoard() {
        return bsBucksLeaderBoard;
    }

    public void setBsBucksLeaderBoard(Location bsBucksLeaderBoard) {
        this.bsBucksLeaderBoard = bsBucksLeaderBoard;
    }

    public void createStaticNPC(String playerName, Location location) {
        if (location == null) return;
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, playerName);
        npc.spawn(location);
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("number3PNJ", number3PNJ);
        config.set("number2PNJ", number2PNJ);
        config.set("number1PNJ", number1PNJ);
        config.set("bsBucksLeaderBoard", bsBucksLeaderBoard);
    }

    public void loadFromConfig(Main instance, Game game, ConfigurationSection config) {
        if (config.contains("number3PNJ")) {
            number3PNJ = config.getLocation("number3PNJ");
        }
        if (config.contains("number2PNJ")) {
            number2PNJ = config.getLocation("number2PNJ");
        }
        if (config.contains("number1PNJ")) {
            number1PNJ = config.getLocation("number1PNJ");
        }
        if (config.contains("bsBucksLeaderBoard")) {
            bsBucksLeaderBoard = config.getLocation("bsBucksLeaderBoard");
        }

        var playersSortedByBsBucks = game.getSortedPlayersByBsBucks();
        if (playersSortedByBsBucks.size() > 0) {
            createStaticNPC(playersSortedByBsBucks.get(0).getPlayerName(), number1PNJ);
        }
        if (playersSortedByBsBucks.size() > 1) {
            createStaticNPC(playersSortedByBsBucks.get(1).getPlayerName(), number2PNJ);
        }
        if (playersSortedByBsBucks.size() > 2) {
            createStaticNPC(playersSortedByBsBucks.get(2).getPlayerName(), number3PNJ);
        }

        if (playersSortedByBsBucks.size() > 0) {
            createLookNPCsTask(instance);
        }


        if (bsBucksLeaderBoard != null) {
            spawnArmorStand(bsBucksLeaderBoard.clone().add(0, 3 + -0.3, 0), ChatColor.GOLD + "=== " + ChatColor.RED + "bsBucks " + ChatColor.YELLOW + "leaderboard " + ChatColor.GOLD + "===");
            for (int i = 0; i < Math.min(playersSortedByBsBucks.size(), 10); i++) {
                fr.louisbillaut.bettersurvival.game.Player player = playersSortedByBsBucks.get(i);
                String playerName = player.getPlayerName();
                String displayName = ChatColor.GOLD + String.valueOf((i + 1)) + ". " + ChatColor.YELLOW + playerName + ChatColor.GRAY + " - " + ChatColor.YELLOW + playersSortedByBsBucks.get(i).getTotalEstimatedFortune();
                spawnArmorStand(bsBucksLeaderBoard.clone().add(0, 3 + -i * 0.3 - 0.6, 0), displayName);
            }
        }
    }

    private BukkitTask createLookNPCsTask(Main instance) {
        return  new BukkitRunnable(){

            @Override
            public void run() {
                for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                    if (npc.getEntity() == null) continue;
                    Location npcLocation = npc.getEntity().getLocation();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Location playerLocation = player.getLocation();
                        double distanceSquared = npcLocation.distanceSquared(playerLocation);
                        if (distanceSquared <= 20 * 20) {
                            npc.faceLocation(playerLocation);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(instance, 0, 20);
    }

    private void spawnArmorStand(Location location, String displayName) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomName(displayName);
        armorStand.setCustomNameVisible(true);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
    }
}
