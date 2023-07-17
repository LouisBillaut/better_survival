package fr.louisbillaut.bettersurvival;

import fr.louisbillaut.bettersurvival.commands.*;
import fr.louisbillaut.bettersurvival.game.Game;
import fr.louisbillaut.bettersurvival.game.Shop;
import fr.louisbillaut.bettersurvival.game.Trade;
import fr.louisbillaut.bettersurvival.listeners.EntityListener;
import fr.louisbillaut.bettersurvival.listeners.PlayerListener;
import fr.louisbillaut.bettersurvival.runnables.ClaimRunnable;
import fr.louisbillaut.bettersurvival.runnables.DailyRewardsRunnable;
import fr.louisbillaut.bettersurvival.utils.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends JavaPlugin {
    private Game game;
    private File dataFile;
    private File settingsFile;
    private FileConfiguration dataConfig;
    private FileConfiguration settings;
    private DiscordWebhook webhook;

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

    public void sendWebhookMessage(String playerName, Shop shop, Trade trade) {
        try {
            webhook.send(playerName, shop, trade);
        } catch (IOException e) {
            Bukkit.getLogger().info("error send webhook: " + e);
        }
    }

    private void deleteVillagerPersistants() {
        Bukkit.getWorlds().forEach(world -> {
            for (Entity entity : world.getEntities()){
                if (entity instanceof Villager){
                    Villager villager = (Villager) entity;
                    if (villager.isInvulnerable()){
                        villager.remove();
                    }
                }
            }
        });
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Loading game ...");
        deleteVillagerPersistants();
        game = new Game();

        settingsFile = new File(getDataFolder(), "settings.yml");
        dataFile = new File(getDataFolder(), "game_better_survival_data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        settings = YamlConfiguration.loadConfiguration(settingsFile);
        String webhookUrl = settings.getString("webhookUrl");
        webhook = new DiscordWebhook(webhookUrl);
        game.loadFromConfig(this, dataConfig);

        PlotCommand plotCommand = new PlotCommand(this, game);
        StuckCommand stuckCommand = new StuckCommand(game);
        ShopCommand shopCommand = new ShopCommand(this, game);
        BucksCommand buckCommand = new BucksCommand(this, game);
        fr.louisbillaut.bettersurvival.commands.HelpCommand helpCommand = new HelpCommand();
        Tab completer = new Tab(game);
        Objects.requireNonNull(getCommand("bshelp")).setExecutor(helpCommand);
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
        game.deleteAllVillagers();
        if (webhook != null && webhook.getUrl() == null) {
            settings.set("webhookUrl", " ");
        } else {
            settings.set("webhookUrl", webhook.getUrl());
        }

        try {
            settings.save(settingsFile);
            dataConfig.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save settings.yml & game_better_survival_data.yml:" + e.getMessage());
        }
        Bukkit.getLogger().info("Game saved !");
    }
}
