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
    protected String name;

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

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public abstract void startAnimation(Main instance, Player player);

    public void stopAnimation() {
        animation.cancel();
    }

    public void handleSneakToggle(Main instance, Player owner, PlayerToggleSneakEvent event) {
        if (event.getPlayer().equals(owner)) {
            if (event.isSneaking()) {
                stopAnimation();
            } else {
                startAnimation(instance, owner);
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

    public static Animation getAnimationFromOriginalName(String name) {
        for (Animation animation: animationsList) {
            if (animation.getName().equals(name)) {
                return animation;
            }
        }

        return null;
    }

    public static void displayAllAnimationsInventory(fr.louisbillaut.bettersurvival.game.Player player) {
        if (player.getBukkitPlayer() == null) return;
        Inventory inventory = Bukkit.createInventory(null, 54, "Animations shop");
        var ownedAnimations = player.getCosmetics().getAnimations();

        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var animationsHead = createAnimationItem();
        inventory.setItem(0, animationsHead);

        int index = 10;
        for(var i = 0; i < animationsList.size(); i++) {
            var item = animationsList.get(i);
            var itemStack = item.getItem().clone();
            if (ownedAnimations.contains(item)) {
                var meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "OWNED " + meta.getDisplayName());
                meta.setLore(new ArrayList<>());
                itemStack.setItemMeta(meta);
            }
            if((index + i) == 16 || (index + i) == 25 || (index + i) == 34 || (index + i) == 43) {
                inventory.setItem(index + i, itemStack);
                index += 2;
            } else {
                inventory.setItem(index + i, itemStack);
            }
        }

        inventory.setItem(45, backItem());
        player.getBukkitPlayer().openInventory(inventory);
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

    public static ItemStack createAnimationItem() {
        var head = Head.getCustomHead(Head.star);
        var meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Animations");
        head.setItemMeta(meta);
        return head;
    }
}
