package com.sidpatchy.ostrich;

import com.sidpatchy.albatross.File.AlbatrossConfiguration;
import com.sidpatchy.ostrich.Listener.PlayerMove;
import com.sidpatchy.ostrich.Util.WorldGuard.FlagRegistry;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * @author Sidpatchy
 * @since June 2022
 * @version 0.1
 */
public final class Ostrich extends JavaPlugin {
    // Related to config parameters and other plugin options
    private boolean successfulLoad;
    private String fallbackLanguageString;
    private boolean checkForUpdates;
    private boolean bStatsEnabled;

    // Related to file handling
    private AlbatrossConfiguration config;
    private AlbatrossConfiguration lang;

    // Listeners
    private PlayerMove playerMove;

    // Related to WorldGuard
    private String flightFlagName;
    private String elytraFlagName;

    // Related to GriefPrevention integration
    private boolean enableGriefPreventionIntegration;
    private boolean griefPreventionEnabled;
    private boolean allowAdminClaimFlight;
    private boolean allowAdminClaimElytra;
    private static DataStore dataStore;
    private String gpFlightFlagName;
    private String gpElytraFlagName;


    @Override
    public void onLoad() {
        extractParametersFromConfig();

        this.getLogger().info("Attempting to register WorldGuard flags.");

        FlagRegistry flagRegistry = new FlagRegistry(this);

        flagRegistry.register(getFlightFlagName());
        flagRegistry.register(getElytraFlagName());

        // Initialize things related to GriefPrevention
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {

            GriefPrevention gp = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
            this.getLogger().info("GriefPrevention is present, enabling related features.");
            griefPreventionEnabled = true;

            if (gp == null) {
                griefPreventionEnabled = false;
                return;
            }

            // Only run if GriefPrevention is enabled
            flagRegistry.register(getGpFlightFlagName());
            flagRegistry.register(getGpElytraFlagName());
        }
        else { griefPreventionEnabled = false; }

        this.getLogger().info("WorldGuard flags registered successfully!");
    }

    @Override
    public void onEnable() {
        playerMove = new PlayerMove(this);

        if (bStatsEnabled) {
            int pluginID = 15562;
            Metrics metrics = new Metrics(this, pluginID);
        }

        // Init things related to GriefPrevention that couldn't be enabled onLoad()
        if (griefPreventionEnabled) {
            GriefPrevention gp = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
            dataStore = gp.dataStore;
        }

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(playerMove, this);

        this.getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args) {
        sender.sendMessage(((Player) sender).getLocale()); // LOL debug code that I haven't replaced with actual features yet
        return true;
    }

    private void extractParametersFromConfig() {
        successfulLoad = true;

        this.getLogger().info("Loading configuration file...");
        try {
            config = new AlbatrossConfiguration("config.yml", this);
            config.loadConfiguration();
        } catch (IOException e) {
            this.getLogger().severe("Error while loading config.yml.");
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe("Error while loading config.yml, check your syntax!");
            throw new RuntimeException(e);
        }

        // General config parameters
        fallbackLanguageString = config.getString("fallbackLanguageString", "eng");
        checkForUpdates = config.getBoolean("checkForUpdates");
        bStatsEnabled = config.getBoolean("bStatsEnabled");

        // Config parameters related to WorldGuard

        // Related to GriefPrevention
        //enableGriefPreventionIntegration = config.getBoolean("enableGriefPreventionIntegration");

        this.getLogger().info("Successfully loaded config.yml!");

        this.getLogger().info("Loading static language elements from lang-" + fallbackLanguageString + ".yml...");
        try {
            lang = new AlbatrossConfiguration("lang-" + fallbackLanguageString + ".yml", this);
            lang.loadConfiguration();
        } catch (IOException e) {
            this.getLogger().severe("Error while loading lang-" + fallbackLanguageString + ".yml");
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe("Error while loading lang-" + fallbackLanguageString + ".yml, check your syntax!");
            throw new RuntimeException(e);
        }

        // Static Language Parameters related to WorldGuard
        flightFlagName = lang.getString("flightFlagName");
        elytraFlagName = lang.getString("elytraFlagName");
        gpFlightFlagName = lang.getString("gpClaimFlightFlagName");
        gpElytraFlagName = lang.getString("gpClaimElytraFlagName");

        this.getLogger().info("Successfully loaded static language elements!");
    }

    // Related to general config options
    public String getFallbackLanguageString() { return fallbackLanguageString; }

    // Related to WorldGuard
    public String getFlightFlagName() { return flightFlagName; }
    public String getElytraFlagName() { return elytraFlagName; }

    // Related to GriefPrevention
    public boolean isGriefPreventionEnabled() { return griefPreventionEnabled; }
    public static DataStore getDataStore() { return dataStore; }
    public String getGpFlightFlagName() { return gpFlightFlagName; }
    public String getGpElytraFlagName() { return gpElytraFlagName; }
}
