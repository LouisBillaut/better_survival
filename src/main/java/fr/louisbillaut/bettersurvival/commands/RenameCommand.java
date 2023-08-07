package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Cosmetics;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.pets.Pet;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static fr.louisbillaut.bettersurvival.game.Cosmetics.renamePetPrice;

public class RenameCommand implements CommandExecutor {

    private Game game;
    private Main instance;

    public RenameCommand(Main instance, Game game) {
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

            if (args.length < 2) {
                player.sendMessage("Use : /rename <pet> <name>");
                return true;
            }

            String pet = args[0].toLowerCase();
            String name = args[1].toLowerCase();
            if (name.equals("") || name.equals(" ")) {
                player.sendMessage(ChatColor.RED + "invalid name.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }
            fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
            if (playerIG == null) return false;
            if((playerIG.getBsBucks() - renamePetPrice) < 0) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "You don't have enough bsBucks");
                return false;
            }
            var ok = playerIG.getCosmetics().renamePet(pet, name);
            if(!ok) {
                player.sendMessage(ChatColor.RED + "invalid command.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }
            playerIG.setBsBucks(playerIG.getBsBucks() - renamePetPrice);
            player.sendMessage(ChatColor.GREEN + "Pet renamed !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You have now " + ChatColor.GOLD + playerIG.getBsBucks() + " bsBucks");
        }

        return false;
    }
}
