package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Cosmetics;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BucksCommand implements CommandExecutor {

    private Game game;
    private Main instance;

    public BucksCommand(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }

    private void showBsBuck(Player player) {
        fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
        if(playerIG == null) return;
        playerIG.showBsBuck();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Use : /bs <buy|show>");
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "sell" -> {
                    game.displayBsBucksInventoryToPlayer(instance, player);
                }
                case "show" -> {
                    showBsBuck(player);
                }
                case "shop" -> {
                    var playerIG = game.getPlayer(player);
                    if (playerIG == null) return true;
                    Cosmetics.displayCosmeticsShop(playerIG);
                }
            }
        }

        return false;
    }
}
