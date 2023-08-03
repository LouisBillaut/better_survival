package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.louisbillaut.bettersurvival.game.Shop.createGlassBlock;

public abstract class Pet {
    public static final String invulnerableTag = "invulnerable";
    protected Main instance;
    protected List<LivingEntity> entities;
    protected boolean isSecret = false;
    protected ItemStack item;
    protected int price;
    protected BukkitTask animation;
    protected BukkitTask followTask;

    public static List<Pet> petsList = new ArrayList<>(List.of(
            new Allay(),
            new Axolotl(),
            new Bee(),
            new Cat(),
            new Chicken(),
            new Cow(),
            new Fox(),
            new Frog(),
            new Goat(),
            new LavaGhost(),
            new MagmaCube(),
            new Panda(),
            new Parrot(),
            new Pig(),
            new PolarBear(),
            new PufferFish(),
            new Rabbit(),
            new Sheep(),
            new Slime(),
            new Villager(),
            new Wolf()
    ));

    public Pet() {

    }
    public Pet(Main instance, Player owner) {
        this.instance = instance;
        this.entities = new ArrayList<>();
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSecret() {
        return isSecret;
    }

    public abstract void spawn(Player player);

    public void despawn() {
        for (LivingEntity entity : entities) {
            if (entity != null && !entity.isDead()) {
                entity.remove();
            }
        }
        if (animation != null) {
            animation.cancel();
        }
        if (followTask != null) {
            followTask.cancel();
        }
        entities.clear();
    }
    protected void startFollowTask(Player owner) {
        followTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entities.isEmpty() || owner == null || owner.isDead()) {
                    this.cancel();
                    return;
                }


                Location ownerLocation = owner.getLocation();
                double playerYaw = Math.toRadians(ownerLocation.getYaw());

                double radius = 1;
                double angleIncrement = (2 * Math.PI) / entities.size();

                double distanceBehind = -0.5;

                for (int i = 0; i < entities.size(); i++) {
                    LivingEntity entity = entities.get(i);
                    double angle = playerYaw + (i * angleIncrement);

                    double x = radius * Math.cos(angle) - distanceBehind * Math.sin(playerYaw);
                    double y = 1.6;
                    double z = radius * Math.sin(angle) + distanceBehind * Math.cos(playerYaw);

                    Location targetLocation = ownerLocation.clone().add(x, y, z);

                    entity.teleport(targetLocation);

                    entity.setVelocity(ownerLocation.getDirection());
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 1);
    }

    public void handleSneakToggle(Player owner, PlayerToggleSneakEvent event) {
        if (event.getPlayer().equals(owner)) {
            if (event.isSneaking()) {
                despawn();
            } else {
                spawn(owner);
            }
        }
    }

    public static void displayAllPetsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Pets shop");

        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, createGlassBlock());
        }

        var sniffer = Head.getCustomHead(Head.snifferEgg);
        ItemMeta meta = sniffer.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Pets");
        sniffer.setItemMeta(meta);
        inventory.setItem(0, sniffer);

        int index = 10;
        for(var i = 0; i < petsList.size(); i++) {
            if (petsList.get(i).isSecret) {
                index --;
                continue;
            }
            if((index + i) == 16 || (index + i) == 25 || (index + i) == 34 || (index + i) == 43) {
                inventory.setItem(index + i, petsList.get(i).getItem());
                index += 2;
            } else {
                inventory.setItem(index + i, petsList.get(i).getItem());
            }
        }

        player.openInventory(inventory);
    }

    public static Pet getPetFromName(String name) {
        for(Pet pet: petsList) {
            if (Objects.requireNonNull(pet.getItem().getItemMeta()).getDisplayName().equals(name)) {
                return pet;
            }
        }

        return null;
    }

    public static void displayValidatePetBuy(Player player, ItemStack itemStack) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Pet Validate");
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
}
