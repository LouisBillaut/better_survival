package fr.louisbillaut.bettersurvival.runnables;

import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Player;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DailyRewardsRunnable extends BukkitRunnable {
    private Plugin plugin;
    private Game game;


    public DailyRewardsRunnable(Plugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void run() {
        for(Player p: game.getPlayers()) {
            if(p.getBukkitPlayer() == null) continue;
            p.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Log in every day to collect daily bsBucks rewards!");
            p.getBukkitPlayer().playSound(p.getBukkitPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
        }
    }

    public void startTask() {
        this.runTaskTimerAsynchronously(plugin, 0, 20 * 60 * 10);
    }
}
