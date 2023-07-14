package fr.louisbillaut.bettersurvival.runnables;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimRunnable extends BukkitRunnable {
    private Plugin plugin;
    private Game game;


    public ClaimRunnable(Plugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void run() {
        for(Player p: game.getPlayers()) {
            if(p.getClaims().size() > 0 && p.getBukkitPlayer() != null) {
                p.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Don't forget to get your claims, use: " + ChatColor.WHITE + ChatColor.BOLD + "/shop claim get");
            }
        }
    }

    public void startTask() {
        this.runTaskTimerAsynchronously(plugin, 0, 20 * 60 * 5);
    }
}
