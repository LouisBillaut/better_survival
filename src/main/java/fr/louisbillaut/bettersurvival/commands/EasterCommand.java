package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Cosmetics;
import fr.louisbillaut.bettersurvival.game.EasterEgg;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static fr.louisbillaut.bettersurvival.listeners.PlayerListener.giveOrDropItem;

public class EasterCommand  implements CommandExecutor {
    private Game game;
    private Main instance;

    public EasterCommand(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You have to be operator to use this command.");
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "dave" -> {
                    game.getEasterEgg().setDaveLocation(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Dave location set !");
                }
                case "lava" -> {
                    giveOrDropItem(player, EasterEgg.getLavaGhostHeadItemEaster(instance));
                }
            }
        }

        return false;
    }
}
