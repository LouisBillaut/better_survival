package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommand implements CommandExecutor {
    private Game game;
    private Main instance;

    public ShopCommand(Main instance, Game game) {
        this.game = game;
        this.instance = instance;
    }


    private void addItemToPlayerShop(Player player, String name, String item, int quantity) {
        var playersInGame = game.getPlayers();
        if (quantity < 1 || quantity > 64) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(ChatColor.RED + "Quantity must be between 1 & 64");
            return;
        }
        for(fr.louisbillaut.bettersurvival.game.Player playerInGame: playersInGame) {
            if (!playerInGame.getPlayerName().equals(player.getDisplayName())) {
                continue;
            }
            Shop shop = playerInGame.getShop(name);
            if (shop == null) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "You don't have a shop named: " + name);
                return;
            }
            if(shop.getTradeList().size() == 5) {
                player.sendMessage(ChatColor.RED + "Max trade limit reached (5).");
                return;
            }
            Material m = Material.matchMaterial(item);
            if(m == null) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "Item " + item + " doesn't exist");
                return;
            }
            ItemStack itemStack = new ItemStack(m);
            itemStack.setAmount(quantity);

            shop.addItemToShop(player, itemStack);
        }
    }

    private void addNewShopToPlayer(Player player, String name) {
        var playersInGame = game.getPlayers();
        for(fr.louisbillaut.bettersurvival.game.Player playerInGame: playersInGame) {
            if (!playerInGame.getPlayerName().equals(player.getDisplayName())) {
                continue;
            }
            if(playerInGame.getShop(name) != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "You already have a shop named: " + name);
                return;
            }
            Shop newShop = new Shop(name);
            playerInGame.addShop(newShop);
            newShop.createShopInventory();
            newShop.displayTrades(player);

            player.sendMessage(ChatColor.GREEN + "Shop " + name + " created !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            return;
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Use : /shop <new|add>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "new" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /shop new <name>");
                        return true;
                    }
                    addNewShopToPlayer(player, args[1]);
                }
                case "list" -> {
                    fr.louisbillaut.bettersurvival.game.Player playerInGame = game.getPlayer(player);
                    if(playerInGame==null) return true;
                    playerInGame.displayListShopInventory();
                }
                case "add" -> {
                    if (args.length < 4) {
                        player.sendMessage("Use: /shop add <name> <itemID> <quantity>");
                        return true;
                    }
                    int quantity;
                    try {
                        quantity = Integer.parseInt(args[3]);
                    } catch(NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "invalid quantity");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        return true;
                    }
                    addItemToPlayerShop(player, args[1], args[2], quantity);
                }
                case "trade" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /shop trade <shop name>");
                        return true;
                    }
                    if (args.length == 2) {
                        fr.louisbillaut.bettersurvival.game.Player playerInGame = game.getPlayer(player);
                        if(playerInGame==null) return true;
                        Shop shop = playerInGame.getShop(args[1]);
                        if(shop == null) {
                            player.sendMessage(ChatColor.RED + "You don't have a shop named: " + args[1]);
                            return true;
                        }
                        shop.displayTrades(player);
                    }
                    if(args.length == 3) {
                        if(args[2].equals("new")) {
                            fr.louisbillaut.bettersurvival.game.Player playerInGame = game.getPlayer(player);
                            if(playerInGame==null) return true;
                            Shop shop = playerInGame.getShop(args[1]);
                            if(shop == null) {
                                player.sendMessage(ChatColor.RED + "You don't have a shop named: " + args[1]);
                                return true;
                            }
                            player.openInventory(shop.getActualInventory());
                        }
                    }
                }
            }
        }
        return false;
    }
}
