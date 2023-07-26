package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand implements CommandExecutor {
    private Main instance;
    private Game game;

    public LeaderboardCommand(Main instance, Game game) {
        this.instance = instance;
        this.game = game;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You have to be operator to use this command.");
            return true;
        }

        if (args.length == 1) {
            player.sendMessage("Usage : /leaderboard <set type of leaderboard> <leaderboard name>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "setpnj":
                if (args.length < 3) {
                    player.sendMessage("Use : /leaderboard setPNJ <leaderboard name> <number>");
                    return true;
                }

                if (args[1].equals("bsBucks")) {
                    if(args[2].equals("1")) {
                        game.getLeaderBoard().setNumber1PNJBsBucks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("2")) {
                        game.getLeaderBoard().setNumber2PNJBsBucks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("3")) {
                        game.getLeaderBoard().setNumber3PNJBsBucks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else {
                        player.sendMessage("Number of PNJ must be between 1 & 3.");
                    }
                }  else if(args[1].equals("playTime")) {
                    if(args[2].equals("1")) {
                        game.getLeaderBoard().setNumber1PNJTimePlayed(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("2")) {
                        game.getLeaderBoard().setNumber2PNJTimePlayed(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("3")) {
                        game.getLeaderBoard().setNumber3PNJTimePlayed(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else {
                        player.sendMessage("Number of PNJ must be between 1 & 3.");
                    }
                } else if(args[1].equals("blocks")) {
                    if(args[2].equals("1")) {
                        game.getLeaderBoard().setNumber1PNJTotalBlocks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("2")) {
                        game.getLeaderBoard().setNumber2PNJTotalBlocks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("3")) {
                        game.getLeaderBoard().setNumber3PNJTotalBlocks(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else {
                        player.sendMessage("Number of PNJ must be between 1 & 3.");
                    }
                } else if(args[1].equals("deaths")) {
                    if(args[2].equals("1")) {
                        game.getLeaderBoard().setNumber1PNJTotalDeaths(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("2")) {
                        game.getLeaderBoard().setNumber2PNJTotalDeaths(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else if (args[2].equals("3")) {
                        game.getLeaderBoard().setNumber3PNJTotalDeaths(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                        break;
                    } else {
                        player.sendMessage("Number of PNJ must be between 1 & 3.");
                    }
                } else {
                    player.sendMessage("This leaderboard type doesn't exist.");
                    return true;
                }
            case "sethologram":
                if (args.length < 2) {
                    player.sendMessage("Use : /leaderboard setHologram <leaderboard name>");
                    return true;
                }
                if (args[1].equals("bsBucks")) {
                    game.getLeaderBoard().setBsBucksLeaderBoard(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                    break;
                } else if (args[1].equals("playTime")) {
                    game.getLeaderBoard().setTimePlayedLeaderBoard(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                    break;
                } else if (args[1].equals("blocks")) {
                    game.getLeaderBoard().setTotalBlocksLeaderBoard(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                    break;
                } else if (args[1].equals("deaths")) {
                    game.getLeaderBoard().setTotalDeathsLeaderBoard(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Position set successfully.");
                    break;
                }
        }

        return true;
    }
}
