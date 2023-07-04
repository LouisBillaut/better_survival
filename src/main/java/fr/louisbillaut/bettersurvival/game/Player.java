package fr.louisbillaut.bettersurvival.game;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private List<Plot> plots;
    private String playerName;

    private org.bukkit.entity.Player bukkitPlayer;

    public Player(String playerName) {
        this.playerName = playerName;
        plots = new ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                bukkitPlayer = p;
            }
        }
    }

    public void setBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Plot getPlot(String name) {
        for(Plot p: plots) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }
    public void addPlot(Plot zone) {
        plots.add(zone);
    }

    public void removePlot(Plot zone) {
        plots.remove(zone);
    }

    public List<Plot> getPlots() {
        return plots;
    }

    public org.bukkit.entity.Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public ConfigurationSection getPlayerConfigByName(String playerName, ConfigurationSection playersSection) {
        for (String playerKey : playersSection.getKeys(false)) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(playerKey);
            String name = playerSection.getString("name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return playerSection;
            }
        }
        return null;
    }

    public void loadFromConfig(ConfigurationSection config) {
        ConfigurationSection c = getPlayerConfigByName(this.playerName, config);
        if (c == null) {
            return;
        }
        if (c.contains("plots")) {
            for (String plotKey : Objects.requireNonNull(c.getConfigurationSection("plots")).getKeys(false)) {
                Plot plot = new Plot();
                plot.loadFromConfig(Objects.requireNonNull(c.getConfigurationSection("plots." + plotKey)));
                plots.add(plot);
            }
        }
    }

    public void saveToConfig(ConfigurationSection config) {
        config.set("name", playerName);

        ConfigurationSection plotsSection = config.createSection("plots");
        for (int i = 0; i < plots.size(); i++) {
            Plot plot = plots.get(i);
            ConfigurationSection plotSection = plotsSection.createSection(String.valueOf(i));
            plotSection.set("name", plot.getName());
            plot.saveToConfig(config.createSection("plots." + i));
        }
    }
}
