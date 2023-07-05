package fr.louisbillaut.bettersurvival.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

public class Game implements Serializable {
    private List<Player> players;
    public Map<UUID, ArrayList<ArmorStand>> armorStands = new HashMap<>();
    public Map<UUID, ArrayList<BukkitRunnable>> armorStandsRotationTasks = new HashMap<>();
    public Game() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayer(org.bukkit.entity.Player bukkitPlayer) {
        for (Player player : players) {
            if (player.getPlayerName().equals(bukkitPlayer.getDisplayName())) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void loadFromConfig(ConfigurationSection config) {
        if (config.contains("players")) {
            ConfigurationSection playersSection = config.getConfigurationSection("players");
            for (String playerKey : Objects.requireNonNull(playersSection).getKeys(false)) {
                ConfigurationSection playerConfig = playersSection.getConfigurationSection(playerKey);

                Player player = new Player(Objects.requireNonNull(playerConfig).getString("name"));
                player.loadFromConfig(playersSection);
                players.add(player);
            }
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        ConfigurationSection playersSection = config.createSection("players");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            ConfigurationSection playerConfig = playersSection.createSection(String.valueOf(i));
            player.saveToConfig(playerConfig);
        }
    }
}
