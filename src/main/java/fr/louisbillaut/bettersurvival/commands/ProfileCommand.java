package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandExecutor {

    private Game game;
    private Main instance;

    public ProfileCommand(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
            if (playerIG == null) return true;
            fr.louisbillaut.bettersurvival.utils.Profile.displayProfileInventory(playerIG);
        }

        return false;
    }
}
