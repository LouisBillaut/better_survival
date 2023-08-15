package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.npcs.Dave;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderBoard {
    private Location number3PNJBsBucks;
    private Location number2PNJBsBucks;
    private Location number1PNJBsBucks;
    private Location bsBucksLeaderBoard;

    private Location number3PNJTimePlayed;
    private Location number2PNJTimePlayed;
    private Location number1PNJTimePlayed;
    private Location timePlayedLeaderBoard;

    private Location number3PNJTotalBlocks;
    private Location number2PNJTotalBlocks;
    private Location number1PNJTotalBlocks;
    private Location totalBlocksLeaderBoard;

    private Location number3PNJTotalDeaths;
    private Location number2PNJTotalDeaths;
    private Location number1PNJTotalDeaths;
    private Location totalDeathsLeaderBoard;

    public LeaderBoard() {}

    public Location getNumber3PNJTotalDeaths() {
        return number3PNJTotalDeaths;
    }

    public void setNumber3PNJTotalDeaths(Location number3PNJTotalDeaths) {
        this.number3PNJTotalDeaths = number3PNJTotalDeaths;
    }

    public Location getNumber2PNJTotalDeaths() {
        return number2PNJTotalDeaths;
    }

    public void setNumber2PNJTotalDeaths(Location number2PNJTotalDeaths) {
        this.number2PNJTotalDeaths = number2PNJTotalDeaths;
    }

    public Location getNumber1PNJTotalDeaths() {
        return number1PNJTotalDeaths;
    }

    public void setNumber1PNJTotalDeaths(Location number1PNJTotalDeaths) {
        this.number1PNJTotalDeaths = number1PNJTotalDeaths;
    }

    public Location getTotalDeathsLeaderBoard() {
        return totalDeathsLeaderBoard;
    }

    public void setTotalDeathsLeaderBoard(Location totalDeathsLeaderBoard) {
        this.totalDeathsLeaderBoard = totalDeathsLeaderBoard;
    }

    public Location getNumber3PNJTotalBlocks() {
        return number3PNJTotalBlocks;
    }

    public void setNumber3PNJTotalBlocks(Location number3PNJTotalBlocks) {
        this.number3PNJTotalBlocks = number3PNJTotalBlocks;
    }

    public Location getNumber2PNJTotalBlocks() {
        return number2PNJTotalBlocks;
    }

    public void setNumber2PNJTotalBlocks(Location number2PNJTotalBlocks) {
        this.number2PNJTotalBlocks = number2PNJTotalBlocks;
    }

    public Location getNumber1PNJTotalBlocks() {
        return number1PNJTotalBlocks;
    }

    public void setNumber1PNJTotalBlocks(Location number1PNJTotalBlocks) {
        this.number1PNJTotalBlocks = number1PNJTotalBlocks;
    }

    public Location getTotalBlocksLeaderBoard() {
        return totalBlocksLeaderBoard;
    }

    public void setTotalBlocksLeaderBoard(Location totalBlocksLeaderBoard) {
        this.totalBlocksLeaderBoard = totalBlocksLeaderBoard;
    }

    public Location getNumber3PNJBsBucks() {
        return number3PNJBsBucks;
    }

    public void setNumber3PNJBsBucks(Location number3PNJBsBucks) {
        this.number3PNJBsBucks = number3PNJBsBucks;
    }

    public Location getNumber2PNJBsBucks() {
        return number2PNJBsBucks;
    }

    public void setNumber2PNJBsBucks(Location number2PNJBsBucks) {
        this.number2PNJBsBucks = number2PNJBsBucks;
    }

    public Location getNumber3PNJTimePlayed() {
        return number3PNJTimePlayed;
    }

    public void setNumber3PNJTimePlayed(Location number3PNJTimePlayed) {
        this.number3PNJTimePlayed = number3PNJTimePlayed;
    }

    public Location getNumber2PNJTimePlayed() {
        return number2PNJTimePlayed;
    }

    public void setNumber2PNJTimePlayed(Location number2PNJTimePlayed) {
        this.number2PNJTimePlayed = number2PNJTimePlayed;
    }

    public Location getNumber1PNJTimePlayed() {
        return number1PNJTimePlayed;
    }

    public void setNumber1PNJTimePlayed(Location number1PNJTimePlayed) {
        this.number1PNJTimePlayed = number1PNJTimePlayed;
    }

    public Location getTimePlayedLeaderBoard() {
        return timePlayedLeaderBoard;
    }

    public void setTimePlayedLeaderBoard(Location timePlayedLeaderBoard) {
        this.timePlayedLeaderBoard = timePlayedLeaderBoard;
    }

    public Location getNumber1PNJBsBucks() {
        return number1PNJBsBucks;
    }

    public void setNumber1PNJBsBucks(Location number1PNJBsBucks) {
        this.number1PNJBsBucks = number1PNJBsBucks;
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
        config.set("number3PNJ", number3PNJBsBucks);
        config.set("number2PNJ", number2PNJBsBucks);
        config.set("number1PNJ", number1PNJBsBucks);
        config.set("bsBucksLeaderBoard", bsBucksLeaderBoard);
        config.set("number1PNJTimePlayed", number1PNJTimePlayed);
        config.set("number2PNJTimePlayed", number2PNJTimePlayed);
        config.set("number3PNJTimePlayed", number3PNJTimePlayed);
        config.set("timePlayedLeaderBoard", timePlayedLeaderBoard);
        config.set("number1PNJTotalBlocks", number1PNJTotalBlocks);
        config.set("number2PNJTotalBlocks", number2PNJTotalBlocks);
        config.set("number3PNJTotalBlocks", number3PNJTotalBlocks);
        config.set("totalBlocksLeaderBoard", totalBlocksLeaderBoard);
        config.set("number1PNJTotalDeaths", number1PNJTotalDeaths);
        config.set("number2PNJTotalDeaths", number2PNJTotalDeaths);
        config.set("number3PNJTotalDeaths", number3PNJTotalDeaths);
        config.set("totalDeathsLeaderBoard", totalDeathsLeaderBoard);
    }

    public static String formatPlayTime(long playTimeInMinutes) {
        long hours = playTimeInMinutes / 60;
        long minutes = playTimeInMinutes % 60;

        return String.format("%02dh %02dm", hours, minutes);
    }

    public void loadLeaderboards(Main instance, Game game, boolean runFromTask) {
        var playersSortedByBsBucks = new ArrayList<>(game.getSortedPlayersByBsBucks());
        if (playersSortedByBsBucks.size() > 0) {
            createStaticNPC(playersSortedByBsBucks.get(0).getPlayerName(), number1PNJBsBucks);
        }
        if (playersSortedByBsBucks.size() > 1) {
            createStaticNPC(playersSortedByBsBucks.get(1).getPlayerName(), number2PNJBsBucks);
        }
        if (playersSortedByBsBucks.size() > 2) {
            createStaticNPC(playersSortedByBsBucks.get(2).getPlayerName(), number3PNJBsBucks);
        }

        var playersSortedByPlayedTime = new ArrayList<>(game.getSortedPlayersByPlayedTime());
        if (playersSortedByPlayedTime.size() > 0) {
            createStaticNPC(playersSortedByPlayedTime.get(0).getPlayerName(), number1PNJTimePlayed);
        }
        if (playersSortedByPlayedTime.size() > 1) {
            createStaticNPC(playersSortedByPlayedTime.get(1).getPlayerName(), number2PNJTimePlayed);
        }
        if (playersSortedByPlayedTime.size() > 2) {
            createStaticNPC(playersSortedByPlayedTime.get(2).getPlayerName(), number3PNJTimePlayed);
        }

        var playersSortedByTotalBlocks = new ArrayList<>(game.getSortedPlayersByTotalBlocks());
        if (playersSortedByTotalBlocks.size() > 0) {
            createStaticNPC(playersSortedByTotalBlocks.get(0).getPlayerName(), number1PNJTotalBlocks);
        }
        if (playersSortedByTotalBlocks.size() > 1) {
            createStaticNPC(playersSortedByTotalBlocks.get(1).getPlayerName(), number2PNJTotalBlocks);
        }
        if (playersSortedByTotalBlocks.size() > 2) {
            createStaticNPC(playersSortedByTotalBlocks.get(2).getPlayerName(), number3PNJTotalBlocks);
        }

        var playersSortedByTotalDeaths = new ArrayList<>(game.getSortedPlayersByTotalDeaths());
        if (playersSortedByTotalDeaths.size() > 0) {
            createStaticNPC(playersSortedByTotalDeaths.get(0).getPlayerName(), number1PNJTotalDeaths);
        }
        if (playersSortedByTotalDeaths.size() > 1) {
            createStaticNPC(playersSortedByTotalDeaths.get(1).getPlayerName(), number2PNJTotalDeaths);
        }
        if (playersSortedByTotalDeaths.size() > 2) {
            createStaticNPC(playersSortedByTotalDeaths.get(2).getPlayerName(), number3PNJTotalDeaths);
        }

        if (playersSortedByBsBucks.size() > 0 || playersSortedByPlayedTime.size() > 0 || playersSortedByTotalBlocks.size() > 0 || playersSortedByTotalDeaths.size() > 0) {
            createLookNPCsTask(instance);
            if (!runFromTask) {
                runUpdateRunnable(instance, game);
            }
        }

        if (bsBucksLeaderBoard != null) {
            spawnArmorStand(bsBucksLeaderBoard.clone().add(0, 3 + -0.3, 0), ChatColor.GOLD + "=== " + ChatColor.RED + "bsBucks " + ChatColor.YELLOW + "leaderboard " + ChatColor.GOLD + "===");
            for (int i = 0; i < Math.min(playersSortedByBsBucks.size(), 10); i++) {
                fr.louisbillaut.bettersurvival.game.Player player = playersSortedByBsBucks.get(i);
                String playerName = player.getPlayerName();
                if(playerName.equals("betterSurvival")) continue; // betterSurvival pseudo is used to create spawn area
                String displayName = ChatColor.GOLD + String.valueOf((i + 1)) + ". " + ChatColor.YELLOW + playerName + ChatColor.GRAY + " - " + ChatColor.YELLOW + playersSortedByBsBucks.get(i).getTotalEstimatedFortune();
                spawnArmorStand(bsBucksLeaderBoard.clone().add(0, 3 + -i * 0.3 - 0.6, 0), displayName);
            }
        }

        if (timePlayedLeaderBoard != null) {
            spawnArmorStand(timePlayedLeaderBoard.clone().add(0, 3 + -0.3, 0), ChatColor.GOLD + "=== " + ChatColor.RED + "play time " + ChatColor.YELLOW + "leaderboard " + ChatColor.GOLD + "===");
            for (int i = 0; i < Math.min(playersSortedByPlayedTime.size(), 10); i++) {
                fr.louisbillaut.bettersurvival.game.Player player = playersSortedByPlayedTime.get(i);
                String playerName = player.getPlayerName();
                if(playerName.equals("betterSurvival")) continue;
                String displayName = ChatColor.GOLD + String.valueOf((i + 1)) + ". " + ChatColor.YELLOW + playerName + ChatColor.GRAY + " - " + ChatColor.YELLOW + formatPlayTime(playersSortedByPlayedTime.get(i).getPlayedTime());
                spawnArmorStand(timePlayedLeaderBoard.clone().add(0, 3 + -i * 0.3 - 0.6, 0), displayName);
            }
        }

        if (totalBlocksLeaderBoard != null) {
            spawnArmorStand(totalBlocksLeaderBoard.clone().add(0, 3 + -0.3, 0), ChatColor.GOLD + "=== " + ChatColor.RED + "total blocks bought " + ChatColor.YELLOW + "leaderboard " + ChatColor.GOLD + "===");
            for (int i = 0; i < Math.min(playersSortedByTotalBlocks.size(), 10); i++) {
                fr.louisbillaut.bettersurvival.game.Player player = playersSortedByTotalBlocks.get(i);
                String playerName = player.getPlayerName();
                if(playerName.equals("betterSurvival")) continue;
                String displayName = ChatColor.GOLD + String.valueOf((i + 1)) + ". " + ChatColor.YELLOW + playerName + ChatColor.GRAY + " - " + ChatColor.YELLOW + playersSortedByTotalBlocks.get(i).getTotalBlocks();
                spawnArmorStand(totalBlocksLeaderBoard.clone().add(0, 3 + -i * 0.3 - 0.6, 0), displayName);
            }
        }

        if (totalDeathsLeaderBoard != null) {
            spawnArmorStand(totalDeathsLeaderBoard.clone().add(0, 3 + -0.3, 0), ChatColor.GOLD + "=== " + ChatColor.RED + "total deaths " + ChatColor.YELLOW + "leaderboard " + ChatColor.GOLD + "===");
            for (int i = 0; i < Math.min(playersSortedByTotalDeaths.size(), 10); i++) {
                fr.louisbillaut.bettersurvival.game.Player player = playersSortedByTotalDeaths.get(i);
                String playerName = player.getPlayerName();
                if(playerName.equals("betterSurvival")) continue;
                String displayName = ChatColor.GOLD + String.valueOf((i + 1)) + ". " + ChatColor.YELLOW + playerName + ChatColor.GRAY + " - " + ChatColor.YELLOW + playersSortedByTotalDeaths.get(i).getDeaths();
                spawnArmorStand(totalDeathsLeaderBoard.clone().add(0, 3 + -i * 0.3 - 0.6, 0), displayName);
            }
        }
    }

    public void loadFromConfig(Main instance, Game game, ConfigurationSection config) {
        if (config.contains("number3PNJ")) {
            number3PNJBsBucks = config.getLocation("number3PNJ");
        }
        if (config.contains("number2PNJ")) {
            number2PNJBsBucks = config.getLocation("number2PNJ");
        }
        if (config.contains("number1PNJ")) {
            number1PNJBsBucks = config.getLocation("number1PNJ");
        }
        if (config.contains("bsBucksLeaderBoard")) {
            bsBucksLeaderBoard = config.getLocation("bsBucksLeaderBoard");
        }
        if (config.contains("number3PNJTimePlayed")) {
            number3PNJTimePlayed = config.getLocation("number3PNJTimePlayed");
        }
        if (config.contains("number2PNJTimePlayed")) {
            number2PNJTimePlayed = config.getLocation("number2PNJTimePlayed");
        }
        if (config.contains("number1PNJTimePlayed")) {
            number1PNJTimePlayed = config.getLocation("number1PNJTimePlayed");
        }
        if (config.contains("timePlayedLeaderBoard")) {
            timePlayedLeaderBoard = config.getLocation("timePlayedLeaderBoard");
        }
        if (config.contains("number3PNJTotalBlocks")) {
            number3PNJTotalBlocks = config.getLocation("number3PNJTotalBlocks");
        }
        if (config.contains("number2PNJTotalBlocks")) {
            number2PNJTotalBlocks = config.getLocation("number2PNJTotalBlocks");
        }
        if (config.contains("number1PNJTotalBlocks")) {
            number1PNJTotalBlocks = config.getLocation("number1PNJTotalBlocks");
        }
        if (config.contains("totalBlocksLeaderBoard")) {
            totalBlocksLeaderBoard = config.getLocation("totalBlocksLeaderBoard");
        }
        if (config.contains("number3PNJTotalDeaths")) {
            number3PNJTotalDeaths = config.getLocation("number3PNJTotalDeaths");
        }
        if (config.contains("number2PNJTotalDeaths")) {
            number2PNJTotalDeaths = config.getLocation("number2PNJTotalDeaths");
        }
        if (config.contains("number1PNJTotalDeaths")) {
            number1PNJTotalDeaths = config.getLocation("number1PNJTotalDeaths");
        }
        if (config.contains("totalDeathsLeaderBoard")) {
            totalDeathsLeaderBoard = config.getLocation("totalDeathsLeaderBoard");
        }

        loadLeaderboards(instance, game, false);
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
                        if(!Objects.equals(npcLocation.getWorld(), playerLocation.getWorld())) continue;
                        double distanceSquared = npcLocation.distanceSquared(playerLocation);
                        if (distanceSquared <= 20 * 20) {
                            npc.faceLocation(playerLocation);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(instance, 0, 20);
    }

    private void runUpdateRunnable(Main instance, Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                CitizensAPI.getNPCRegistries().forEach(n ->{
                            n.sorted().forEach(npc -> {
                                npc.despawn();
                                npc.destroy();
                                npc.getTrait(Owner.class).onRemove();
                            });
                            n.deregisterAll();
                            n.saveToStore();
                        }
                );
                instance.removeInvisibleArmorStands();
                loadLeaderboards(instance, game, true);
                Dave.spawn(game.getEasterEgg().getDaveLocation());
            }
        }.runTaskTimer(instance, 20 * 60 * 15, 20 * 60 * 15);
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
