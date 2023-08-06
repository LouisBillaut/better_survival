package fr.louisbillaut.bettersurvival.pets;

import fr.louisbillaut.bettersurvival.Main;
import fr.louisbillaut.bettersurvival.utils.Head;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class LavaGhost extends Pet {
    public LavaGhost() {
        price = 0;
        ItemStack block = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta itemMeta = block.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Lava Ghost");
        block.setItemMeta(itemMeta);
        item = block;
        isSecret = true;
        name = "lavaGhost";
    }
    public LavaGhost(Main instance, Player owner) {
        super(instance, owner);
        price = 0;
        ItemStack block = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta itemMeta = block.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Lava Ghost");
        item = block;
        isSecret = true;
        name = "lavaGhost";
    }
    @Override
    public void spawn(Main instance, Player owner) {
        ItemStack redLeatherLegging = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) redLeatherLegging.getItemMeta();
        helmetMeta.setColor(Color.RED);
        redLeatherLegging.setItemMeta(helmetMeta);

        ItemStack redLeatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) redLeatherChestplate.getItemMeta();
        chestplateMeta.setColor(Color.RED);
        redLeatherChestplate.setItemMeta(chestplateMeta);

        ItemStack redLeatherBoots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) redLeatherBoots.getItemMeta();
        bootsMeta.setColor(Color.RED);
        redLeatherChestplate.setItemMeta(bootsMeta);

        var head = Head.getCustomHead(Head.ghostLava);

        Location location = owner.getLocation();
        org.bukkit.entity.ArmorStand armorStand = (org.bukkit.entity.ArmorStand) owner.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setBasePlate(false);
        armorStand.setSmall(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setAI(false);

        armorStand.getEquipment().setLeggings(redLeatherLegging);
        armorStand.getEquipment().setChestplate(redLeatherChestplate);
        armorStand.getEquipment().setBoots(redLeatherBoots);
        armorStand.getEquipment().setHelmet(head);

        armorStand.addScoreboardTag(invulnerableTag);

        entities.add(armorStand);
        animation = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    showParticleAnimation();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 20);

        startFollowTask(instance, owner);
    }

    private void showParticleAnimation() throws InterruptedException {
        if (entities.size() > 0) {
            int numParticles = 10;
            for (int i = 0; i < numParticles; i++) {
                if (entities.size() == 0 || entities.get(0) == null) {
                    return;
                }
                Location location = entities.get(0).getLocation();
                World world = location.getWorld();

                double radius = 0.4;
                double angle = 2 * Math.PI * i / numParticles;
                double x = location.getX() + radius * Math.cos(angle);
                double y = location.getY() + 0.6 + radius * Math.sin(angle);
                double z = location.getZ() + radius * Math.sin(angle);
                Location particleLocation = new Location(world, x, y, z);
                world.spawnParticle(Particle.FLAME, particleLocation, 0, 0, 0, 0, 0);
                Thread.sleep(100);
            }
        }
    }
}
