package com.gageryanplugins.lumberjack;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lumberjack extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        final Logger logger = this.getLogger(); // Part of the update checker

        new UpdateChecker(this, 64657).getVersion(version -> { // Update checker
            if (this.isHigherOrEqual(this.getDescription().getVersion(), version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available.");
            }
        });

        final String minVersion = "1.13.0";

        if (!this.isVersionHigherOrEqual(minVersion, this.getServer().getBukkitVersion().split("-")[0])) {
            this.getLogger().log(Level.SEVERE, "Your server version is not supported! " +
                    "Plugin needs at least server version " + minVersion + " to work!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Create config-file
        this.createConfig();

        // Register Listener
        this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    }

    private void createConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();

        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.getLogger().info("Config.yml not found, creating!");
            this.saveDefaultConfig();
            return;
        }

        this.getLogger().info("Config.yml found, loading!");

        // Converter for config version 1 -> newest version is 2
        if (!this.getConfig().isSet("config-version")) {

            final List<String> oldTools = this.getConfig().getStringList("tools");

            for (int i = 0; i < oldTools.size(); i++) {
                final String tool = oldTools.get(i);

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

            this.getConfig().set("config-version", 2);
        }
    }

    private boolean isVersionHigherOrEqual(final String minVersion, final String targetVersion) {
        final String[] minParts = minVersion.split("\\.");
        final String[] serverParts = targetVersion.split("\\.");

        for (int i = 0; i < 2; i++) {
            System.out.println(minParts[i]);
            System.out.println(serverParts[i]);
            if (!this.isHigherOrEqual(minParts[i], serverParts[i])) return false;
        }

        return true;
    }

    private boolean isHigherOrEqual(final String min, final String check) {
        if (Integer.parseInt(check) >= Integer.parseInt(min)) return true;
        return false;
    }
}