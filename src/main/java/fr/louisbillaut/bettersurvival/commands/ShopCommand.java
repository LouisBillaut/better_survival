package fr.louisbillaut.bettersurvival.commands;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Shop;
import fr.louisbillaut.bettersurvival.utils.Selector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
            if(shop.getTradeList().size() == Shop.getMaxTradeLimit()) {
                player.sendMessage(ChatColor.RED + "Max trade limit reached: " + Shop.getMaxTradeLimit());
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
            if (playerInGame.getShops().size() >= fr.louisbillaut.bettersurvival.game.Player.getMaxShops()) {
                player.sendMessage(ChatColor.RED + "Maximum number of shops reached: " + fr.louisbillaut.bettersurvival.game.Player.getMaxShops());
                return;
            }
            Shop newShop = new Shop(name);
            playerInGame.addShop(newShop);
            newShop.createShopInventory();
            newShop.displayTrades(instance, player);

            player.sendMessage(ChatColor.GREEN + "Shop " + name + " created !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            return;
        }
    }

    private void showShopToPlayer(Player player, String name) {
        var playerInGame = game.getPlayer(player);
        if (player == null) return;
        Shop shop = playerInGame.getShop(name);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "You don't have a shop named: " + name);
            return;
        }
        shop.displayTrades(instance, player);
    }

    private void claimShop(Player player) {
        fr.louisbillaut.bettersurvival.game.Player playerInGame = game.getPlayer(player);
        if(playerInGame == null) return;
        if(playerInGame.getClaims().size() == 0) {
            player.sendMessage(ChatColor.RED + "you don't have any item to claim");
            return;
        }
        player.setMetadata("claimShop", new FixedMetadataValue(instance, true));
        Selector.appearArmorStand(instance, game, player);
    }

    private void displayClaims(Player player) {
        fr.louisbillaut.bettersurvival.game.Player playerInGame = game.getPlayer(player);
        if(playerInGame == null) return;
        playerInGame.displayClaims();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Usage: /shop <claim|show|new|list|add|trade>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "claim" -> {
                    if (args.length < 2) {
                        player.sendMessage("Usage: /shop claim <list|get>");
                        return true;
                    }
                    if (args[1].equals("get")) {
                        claimShop(player);
                        return true;
                    }
                    if (args[1].equals("list")) {
                        displayClaims(player);
                        return true;
                    }
                }
                case "show" -> {
                    if (args.length < 2) {
                        player.sendMessage("Use : /shop show <name>");
                        return true;
                    }
                    showShopToPlayer(player, args[1]);
                }
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
                    if (args.length == 1) {
                        playerInGame.displayListShopInventory();
                        return true;
                    } else if (args.length == 2 && args[1].equals("all")) {
                        playerInGame.displayAllTrades(instance, game);
                        return true;
                    } else {
                        player.sendMessage("Use : /shop list [all]");
                        return true;
                    }
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
                        shop.displayTrades(instance, player);
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
