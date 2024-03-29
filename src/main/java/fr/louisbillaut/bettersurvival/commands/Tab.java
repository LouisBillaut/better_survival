package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Plot;
import fr.louisbillaut.bettersurvival.game.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tab implements TabCompleter {
    private Game game;
    public Tab(Game game) {
        this.game = game;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("plot")) {
            if (args.length == 1) {
                return Arrays.asList("new", "list", "show", "settings");
            }
            if (args.length == 2 && (args[0].equals("show") || (args[0].equals("settings")))) {
                if (!(sender instanceof Player)) return null;
                Player player = (Player) sender;
                fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
                if (playerIG == null) return null;
                return playerIG.getPlots().stream().map(Plot::getName).collect(Collectors.toCollection(ArrayList::new));
            }
            if (args.length == 5 && (args[3].equals("add") || args[3].equals("remove"))) {
                return StringUtil.copyPartialMatches(args[4], game.getPlayers().stream().map(fr.louisbillaut.bettersurvival.game.Player::getPlayerName).collect(Collectors.toCollection(ArrayList::new)), new ArrayList<>());
            }
        } else if (command.getName().equalsIgnoreCase("shop")) {
            if (args.length == 1) {
                return Arrays.asList("claim", "new", "list", "add", "trade");
            }
            if (args.length == 2 && args[0].equals("list")) {
                return Arrays.asList("all");
            }
            if (args.length == 2 && (args[0].equals("claim"))) {
                return Arrays.asList("get", "list");
            }
            if (!(sender instanceof Player)) return null;
            Player player = (Player) sender;
            fr.louisbillaut.bettersurvival.game.Player playerIG = game.getPlayer(player);
            if (playerIG == null) return null;
            if (args.length == 2 && args[0].equals("trade") ) {
                return playerIG.getShops().stream().map(Shop::getName).collect(Collectors.toCollection(ArrayList::new));
            }
            if (args.length == 2 && args[0].equals("add")) {
                return playerIG.getShops().stream().map(Shop::getName).collect(Collectors.toCollection(ArrayList::new));
            }
            if (args.length == 3 && args[0].equals("add")) {
                return StringUtil.copyPartialMatches(args[2], getAllItemNames(), new ArrayList<>());
            }
        } else if (command.getName().equalsIgnoreCase("bs")) {
            if (args.length == 1) {
                return Arrays.asList("sell", "show", "shop");
            }
        } else if (command.getName().equalsIgnoreCase("compass")) {
            if (args.length == 1) {
                return Arrays.asList("clear");
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<String> getAllItemNames() {
        ArrayList<String> itemNames = new ArrayList<>();

        for (Material material : Material.values()) {
            itemNames.add(material.name());
        }

        return itemNames;
    }
}
