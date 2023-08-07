package fr.louisbillaut.bettersurvival.game;

import fr.louisbillaut.bettersurvival.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Scoreboard {
    private BukkitTask runnable;
    private final int reloadTimeSec = 1;
    private ScoreboardManager scoreboardManager;
    private org.bukkit.scoreboard.Scoreboard scoreboard;
    private Objective objective;
    private String scoreboardTitle = "Better Survival";
    public Scoreboard(Main instance, Player player) {
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("BetterSurvival", "dummy", ChatColor.DARK_PURPLE + "Better Survival");
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getBukkitPlayer() != null) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(instance, 0, reloadTimeSec * 20);
    }

    public BukkitTask getRunnable() {
        return runnable;
    }

    public void updateScoreboard(Player player) {
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("BetterSurvival", "dummy", ChatColor.DARK_PURPLE + "Better Survival");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + scoreboardTitle);

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d");
        String currentDate = dateFormat.format(new Date());

        objective.getScore(ChatColor.GRAY + "     " + currentDate).setScore(11);
        objective.getScore("").setScore(10);
        objective.getScore(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + player.getPlayerName()).setScore(9);
        objective.getScore(ChatColor.GRAY + ">>" + ChatColor.WHITE + " PlayTime: " + ChatColor.AQUA + LeaderBoard.formatPlayTime(player.getPlayedTime())).setScore(8);
        objective.getScore(ChatColor.GRAY + ">>" + ChatColor.WHITE + " Deaths: " + ChatColor.RED + player.getDeaths()).setScore(7);
        objective.getScore(ChatColor.GRAY + ">>" + ChatColor.WHITE + " bsBucks: " + ChatColor.GOLD + player.getBsBucks()).setScore(6);
        objective.getScore("").setScore(5);
        objective.getScore(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Plots").setScore(3);
        objective.getScore(ChatColor.GRAY + ">>" + ChatColor.WHITE + "Nb: " + ChatColor.AQUA + player.getPlots().size()).setScore(2);
        objective.getScore(ChatColor.GRAY + ">>" + ChatColor.WHITE + "Blocks: " + ChatColor.AQUA + player.getTotalBlocks()).setScore(1);

        player.getBukkitPlayer().setScoreboard(scoreboard);
    }
}
