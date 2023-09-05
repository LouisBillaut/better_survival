package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Plot;
import fr.louisbillaut.bettersurvival.game.Shop;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class PlotCommand implements CommandExecutor {
    private Game game;
    private Main instance;
    public PlotCommand(Main instance, Game game) {
        this.instance = instance;
        this.game = game;
    }

    private void createPlot(Player player, String name) {
        fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
        if (playerIG == null) return;
        if(playerIG.hasPlotWithName(name)) {
            player.sendMessage(ChatColor.RED + "you already have a plot named " + name);
            return;
        }
        player.setMetadata("plotName", new FixedMetadataValue(instance, name));
        Plot.showPlotHeightOptions(player);
    }

    private void listPlots(Player player) {
        fr.louisbillaut.bettersurvival.game.Player gamePlayer = game.getPlayer(player);
        if (gamePlayer != null) {
            Plot.displayPlotTypeInventory(gamePlayer);
        } else {
            player.sendMessage("You must be connected to use this command.");
        }
    }

    private void showPlot(Player player, String plot) {
        fr.louisbillaut.bettersurvival.game.Player gamePlayer = game.getPlayer(player);
        if (gamePlayer != null && gamePlayer.getPlot(plot) != null) {
            gamePlayer.getPlot(plot).show(instance, player);
            player.sendMessage("Plot " + plot + " showed !");
        }
    }

    private boolean plotSettings(Player player, String p) {
        Plot plot = game.getPlayer(player).getPlot(p);
        if (plot == null) {
            player.sendMessage("Your don't have a plot named " + p);
            return true;
        }
        plot.openSettingsInventory(instance, player);
        return false;
    }

    private void plotSubSetting(Player player, String plot, String whitelistType, String action, String playerName) {
        fr.louisbillaut.bettersurvival.game.Player gamePlayer = game.getPlayer(player);
        if (gamePlayer != null && gamePlayer.getPlot(plot) != null) {
            Plot targetPlot = gamePlayer.getPlot(plot);

            boolean b = whitelistType.equals("buildWhitelist") || whitelistType.equals("enterWhitelist") || whitelistType.equals("interactWhitelist");
            if (action.equalsIgnoreCase("add")) {
                if(b) {
                    targetPlot.addPlayerToWhitelist(playerName, whitelistType);
                    player.sendMessage("Player " + playerName + " added to " + whitelistType + " !");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                }else {
                    player.sendMessage("Invalid whitelist. Whitelist " + whitelistType + " doesn't exist.");
                }
                plotSettings(player, plot);
            } else if (action.equalsIgnoreCase("remove")) {
                if(b) {
                    targetPlot.removePlayerToWhitelist(playerName, whitelistType);
                    player.sendMessage("Player " + playerName + " removed from " + whitelistType + " !");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                }else {
                    player.sendMessage("Invalid whitelist. Whitelist " + whitelistType + " doesn't exist.");
                }
                plotSettings(player, plot);
            } else {
                player.sendMessage("Invalid action type. Use 'add' or 'remove' as action.");
            }
        } else {
            player.sendMessage(Main.sendLocalizedMessage("errPlotNamed") + plot + ".");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Usage: /plot <new|list|show|settings> <name>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "new" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /plot new <name>");
                        return true;
                    }
                    createPlot(player, args[1]);
                    return true;
                }
                case "list" -> {
                    listPlots(player);
                    return true;
                }
                case "show" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /plot show <name>");
                        return true;
                    }
                    showPlot(player, args[1]);
                }
                case "settings" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /plot settings <name>");
                        return true;
                    }
                    if (args.length == 2) {
                        return plotSettings(player, args[1]);
                    }
                    if (args.length < 5) {
                        player.sendMessage("Use: /plot settings <name> <whitelist type> <add|remove> <player name>");
                        return true;
                    }
                    plotSubSetting(player, args[1], args[2], args[3], args[4]);
                    return true;
                }
                default -> {
                    player.sendMessage("Unknown command. Usage: /plot <new|list|show|settings> [name]");
                    return true;
                }
            }
        }

        return false;
    }
}
