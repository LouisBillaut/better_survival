package fr.louisbillaut.bettersurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class HelpCommand implements CommandExecutor {

    public void giveCommandBook(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setTitle("Command Book");
        meta.setAuthor("Server");

        meta.addPage(
                "Command: " + ChatColor.GREEN +"/plot\n" + ChatColor.GRAY + "Create or list plots\n" +
                        "Subcommands:\n" +
                        "- " + ChatColor.GREEN + "new: " + ChatColor.GRAY + "Create a new plot. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN + "/plot new <name>\n" +
                        "- " + ChatColor.GREEN + "list: " + ChatColor.GRAY + "List active plots. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/plot list"
        );

        meta.addPage(
                "Command: " + ChatColor.GREEN +"/shop\n" + ChatColor.GRAY + "Create or list shop\n" +
                        "Subcommands:\n" +
                        ChatColor.BLACK + "- " + ChatColor.GREEN + "new: " + ChatColor.GRAY + "Create a new shop. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN + "/shop new <name>\n" +
                        ChatColor.BLACK +  "- " + ChatColor.GREEN + "list: " + ChatColor.GRAY + "List shops. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/shop list"
        );
        meta.addPage(
                ChatColor.BLACK + "- " + ChatColor.GREEN + "trade: " + ChatColor.GRAY + "List your trades in shops. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/shop trade <shop name> [optional: new]" +ChatColor.BLACK + "(new to add new trade)" +
                        ChatColor.BLACK + "\n- " + ChatColor.GREEN + "add: " + ChatColor.GRAY + "Add an item to a trade. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/shop add <shop name> <item name> <item quantity>"
        );
        meta.addPage(
                ChatColor.BLACK + "- " + ChatColor.GREEN + "claim: " + ChatColor.GRAY + "Allow you to claims rewards of your sales. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/shop claim <list|get>" + ChatColor.BLACK + "(list to check your claims and get to get them)"
        );

        meta.addPage(
                "Command: " + ChatColor.GREEN +"/bs\n" + ChatColor.GRAY + "show or buy bsBucks\n" +
                        "Subcommands:\n" +
                        ChatColor.BLACK + "- " + ChatColor.GREEN + "show: " + ChatColor.GRAY + "Show your bsBucks. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN + "/bs show\n" +
                        ChatColor.BLACK + "- " + ChatColor.GREEN + "buy: " + ChatColor.GRAY + "Display the bsBucks Market. \n" + ChatColor.BLACK + "Usage: " + ChatColor.GREEN +  "/bs buy"
        );

        meta.addPage(
                "Command: " + ChatColor.GREEN +"/stuck\n" + ChatColor.GRAY + "unstuck you\n" +
                        "Use only if you are stuck in a plot\n" +
                        "teleport you to your spawn\n"
        );

        book.setItemMeta(meta);

        player.openBook(book);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            giveCommandBook(player);
            return true;
        }
        return false;
    }

}
