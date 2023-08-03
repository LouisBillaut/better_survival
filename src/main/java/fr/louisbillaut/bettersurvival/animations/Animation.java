package fr.louisbillaut.bettersurvival.animations;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;

public abstract class Animation {
    protected Main instance;
    protected BukkitTask animation;
    protected ItemStack item;
    protected int price;

    public static List<Animation> animationsList = new ArrayList<>(List.of(
            new Angry(),
            new Aureole(),
            new Happy(),
            new Heart(),
            new Music(),
            new Smoke(),
            new Spell(),
            new Storm()
    ));

    public Animation(){}
    public Animation(Main instance) {
        this.instance = instance;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public abstract void startAnimation(Player player);

    public void stopAnimation() {
        animation.cancel();
    }

    public void handleSneakToggle(Player owner, PlayerToggleSneakEvent event) {
        if (event.getPlayer().equals(owner)) {
            if (event.isSneaking()) {
                stopAnimation();
            } else {
                startAnimation(owner);
            }
        }
    }

    public static Animation getAnimationFromName(String name) {
        for (Animation animation: animationsList) {
            if (Objects.requireNonNull(animation.getItem().getItemMeta()).getDisplayName().equals(name)) {
                return animation;
            }
        }

        return null;
    }

    public static void displayAllAnimationsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Animations shop");

        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var animationsHead = createAnimationItem();
        inventory.setItem(0, animationsHead);

        int index = 10;
        for(var i = 0; i < animationsList.size(); i++) {
            if((index + i) == 16 || (index + i) == 25 || (index + i) == 34 || (index + i) == 43) {
                inventory.setItem(index + i, animationsList.get(i).getItem());
                index += 2;
            } else {
                inventory.setItem(index + i, animationsList.get(i).getItem());
            }
        }

        player.openInventory(inventory);
    }

    public static void displayValidateAnimationBuy(Player player, ItemStack itemStack) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Animation Validate");
        for (int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        inventory.setItem(12, itemStack);
        inventory.setItem(14, createBuyItem());
        inventory.setItem(18, backItem());
        player.openInventory(inventory);
    }

    private static ItemStack backItem() {
        var head = Head.getCustomHead(Head.quartzArrowLeft);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "back");
        head.setItemMeta(meta);

        return head;
    }

    private static ItemStack createBuyItem() {
        ItemStack pageItem = new ItemStack(Material.SLIME_BALL);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "buy");
        pageItem.setItemMeta(pageMeta);
        return pageItem;
    }

    private static ItemStack createAnimationItem() {
        var head = Head.getCustomHead(Head.star);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Animations");
        head.setItemMeta(meta);
        return head;
    }
}
