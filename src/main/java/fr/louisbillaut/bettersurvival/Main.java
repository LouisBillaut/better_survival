package fr.louisbillaut.bettersurvival;

import fr.louisbillaut.bettersurvival.commands.*;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.listeners.EntityListener;
import fr.louisbillaut.bettersurvival.listeners.PlayerListener;
import fr.louisbillaut.bettersurvival.runnables.ClaimRunnable;
import fr.louisbillaut.bettersurvival.runnables.DailyRewardsRunnable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends JavaPlugin {
    private Game game;
    private File dataFile;
    private FileConfiguration dataConfig;
    private static ResourceBundle messagesBundle;

    private void initializeListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, game), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
    }

    private void initializeRunnables() {
        new ClaimRunnable(this, game).startTask();
        new DailyRewardsRunnable(this, game).startTask();
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public static String sendLocalizedMessage(String key) {
        Locale locale = new Locale("fr");
        ResourceBundle playerBundle = ResourceBundle.getBundle("messages", locale);
        String template = playerBundle.getString(key);

        return template;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Loading game ...");
        game = new Game();

        dataFile = new File(getDataFolder(), "game_better_survival_data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        game.loadFromConfig(this, dataConfig);

        PlotCommand plotCommand = new PlotCommand(this, game);
        StuckCommand stuckCommand = new StuckCommand(game);
        ShopCommand shopCommand = new ShopCommand(this, game);
        BucksCommand buckCommand = new BucksCommand(this, game);
        Tab completer = new Tab(game);
        Objects.requireNonNull(getCommand("plot")).setExecutor(plotCommand);
        Objects.requireNonNull(getCommand("plot")).setTabCompleter(completer);
        Objects.requireNonNull(getCommand("stuck")).setExecutor(stuckCommand);
        Objects.requireNonNull(getCommand("shop")).setExecutor(shopCommand);
        Objects.requireNonNull(getCommand("shop")).setTabCompleter(completer);
        Objects.requireNonNull(getCommand("bs")).setExecutor(buckCommand);
        Objects.requireNonNull(getCommand("bs")).setTabCompleter(completer);

        initializeListeners();
        initializeRunnables();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Saving game ...");
        game.saveToConfig(dataConfig);
        game.cancelAllVillagersTasks();
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Game saved !");
    }
}
