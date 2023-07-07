package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Plot;
import fr.louisbillaut.bettersurvival.utils.Detector;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StuckCommand implements CommandExecutor {
    private Game game;

    public StuckCommand(Game game) {
        this.game = game;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            var playersInGame = game.getPlayers();
            for(fr.louisbillaut.bettersurvival.game.Player playerIG: playersInGame) {
                if(player.getDisplayName().equals(playerIG.getPlayerName())) continue;
                var plots = playerIG.getPlots();
                for(Plot p: plots) {
                    if(p.getPlayerEnter().equals(Plot.PlotSetting.ACTIVATED)) {
                        continue;
                    }
                    if(Detector.isInZone(player.getLocation(), p.getLocation1(), p.getLocation2(), p.getHeight())
                            && p.getPlayerEnter().equals(Plot.PlotSetting.DEACTIVATED)) {
                        player.teleport(player.getBedSpawnLocation());
                        player.sendMessage(Main.sendLocalizedMessage("unstuck"));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
                    }
                    if(Detector.isInZone(player.getLocation(), p.getLocation1(), p.getLocation2(), p.getHeight())
                            && p.getPlayerEnter().equals(Plot.PlotSetting.CUSTOM)
                            && !Detector.isInWhiteList(player, p.getPlayerEnterWhitelist())) {
                        player.teleport(player.getBedSpawnLocation());
                        player.sendMessage("You are now unstuck !");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
                    }
                }
            }
        }

        return true;
    }
}
