package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompassCommand implements CommandExecutor {

    private Game game;
    private Main instance;

    public CompassCommand(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Use : /compass clear");
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "clear" -> {
                    var playerIG = game.getPlayer(player);
                    if (playerIG == null) return false;
                    playerIG.clearCompass();
                }
            }
        }

        return false;
    }
}
