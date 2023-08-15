package fr.louisbillaut.bettersurvival.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dave {
    public static class DaveTrait extends Trait {
        private Map<Player, Long> cooldownMap = new HashMap<>();
        private static final long MESSAGE_COOLDOWN = 60000;
        private Random random = new Random();
        private String text0 = "Bonjour voyageur, connais la légende du " + ChatColor.RED + " lava ghost ?";
        private String text1 = "Jadis, une civilisation était renommée pour sa maîtrise inégalée des arts de la forge et de la magie, et c'est au sommet de sa grandeur que le Lava Ghost fut conçu.";
        private String text2 = "Au crépuscule de leur ère florissante, les forgerons prodigieux de cette civilisation travaillèrent sans relâche pour créer le Lava Ghost, une œuvre d'ingénierie et de magie fusionnées. Le Lava Ghost était conçu pour canaliser l'énergie ardente de la terre elle-même, capable de contrôler la lave et le feu à une échelle jamais vue auparavant. Sa mission était de protéger et d'enrichir les terres fertiles que les ancêtres avaient chéries.";
        private String text3 = "Cependant, la civilisation finit par tomber sous l'ombre d'une obscurité qu'elle n'avait pu prévoir. Une catastrophe dévastatrice frappa, plongeant les terres dans le chaos et la ruine. Le Lava Ghost, dans un acte désespéré pour empêcher la destruction totale, se divisa en deux fragments distincts, chacun renfermant une partie de son pouvoir et de son essence.";
        private String text4 = "Moi, Dave, je suis à la recherche de cette technologie perdue.";

        private String easter1 = "Je sais que le premier morceau du Lava Ghost est sur cette île ! Je suis tout prêt du but.";
        public DaveTrait() {
            super("dave");
        }

        private void sendLegend(NPCRightClickEvent event) {
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + text0);
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + text1);
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + text2);
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + text3);
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + text4);
        }

        private void sendEaster(NPCRightClickEvent event) {
            event.getClicker().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Dave" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + easter1);
        }
        @EventHandler
        public void onRightClick(NPCRightClickEvent event) {
            if (event.getNPC() == getNPC()) {
                long currentTime = System.currentTimeMillis();
                if (!cooldownMap.containsKey(event.getClicker()) || currentTime - cooldownMap.get(event.getClicker()) >= MESSAGE_COOLDOWN) {
                    int randomNumber = random.nextInt(5) + 1;
                    if (randomNumber == 2) {
                        sendEaster(event);
                    }else {
                        sendLegend(event);
                    }
                    cooldownMap.put(event.getClicker(), currentTime);
                }
            }
        }
    }

    public static void spawn(Location location) {
        NPC dave = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Dave");

        dave.addTrait(new DaveTrait());

        dave.spawn(location);
    }
}
