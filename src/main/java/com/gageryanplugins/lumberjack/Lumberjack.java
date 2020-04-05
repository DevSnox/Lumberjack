package com.gageryanplugins.lumberjack;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Lumberjack extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
    	
    	Logger logger = this.getLogger(); // Part of the update checker
    	 
        new UpdateChecker(this, 12345).getVersion(version -> { // Update checker
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available.");
            }
        }); 	
    	
        String minVersion = "1.13.0";

        if(!isServerVersionHigherOrEqual(minVersion, this.getServer().getBukkitVersion().split("-")[0])) {
            this.getLogger().log(Level.SEVERE, "Your server version is not supported! " +
                    "Plugin needs at least server version " + minVersion + " to work!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Create config-file
        createConfig();

        // Register Listener
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    }

    private void createConfig() {
        if(!this.getDataFolder().exists()) getDataFolder().mkdirs();

        if (!new File(getDataFolder(), "config.yml").exists()) {
            getLogger().info("Config.yml not found, creating!");
            saveDefaultConfig();
            return;
        }

        getLogger().info("Config.yml found, loading!");

        // Converter for config version 1 -> newest version is 2
        if(!getConfig().isSet("config-version")) {

            List<String> oldTools = getConfig().getStringList("tools");

            for(int i = 0; i < oldTools.size(); i++) {
                String tool = oldTools.get(i);

                switch (tool) {
                    case "wooden":
                        oldTools.set(i, "WOODEN_AXE");
                        break;
                    case "stone":
                        oldTools.set(i, "STONE_AXE");
                        break;
                    case "iron":
                        oldTools.set(i, "IRON_AXE");
                        break;
                    case "golden":
                        oldTools.set(i, "GOLDEN_AXE");
                        break;
                    case "diamond":
                        oldTools.set(i, "DIAMOND_AXE");
                        break;
                }
            }

            this.getLogger().log(Level.INFO, "Changed configuration to version 2! Now supporting all materials as tools.");

            getConfig().set("config-version", 2);
        }
    }

    private boolean isServerVersionHigherOrEqual(String minVersion, String serverVersion) {
        String[] minParts = minVersion.split("\\.");
        String[] serverParts = serverVersion.split("\\.");

        for(int i = 0; i < 2; i++) {
            if(!isHigherOrEqual(minParts[i], serverParts[i])) return false;
        }

        return true;
    }

    private boolean isHigherOrEqual(String min, String check) {
        if(Integer.parseInt(check) >= Integer.parseInt(min)) return true;
        return false;
    }
}
