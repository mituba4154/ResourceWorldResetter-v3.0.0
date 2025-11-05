package com.lozaine.ResourceWorldResetter.worldmanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating the appropriate WorldManagerAdapter based on available plugins
 */
public class WorldManagerAdapterFactory {
    
    /**
     * Create the best available world manager adapter
     * Priority: Multiverse-Core 5.x > Multiverse-Core 4.x > Bukkit Native
     * 
     * @param logger Logger for diagnostic messages
     * @return WorldManagerAdapter instance
     */
    public static WorldManagerAdapter createAdapter(Logger logger) {
        // Try Multiverse-Core 5.x first
        Plugin mvPlugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        
        if (mvPlugin != null && mvPlugin.isEnabled()) {
            String version = mvPlugin.getDescription().getVersion();
            logger.info("Detected Multiverse-Core version: " + version);
            
            // Check if it's version 5.x
            if (version.startsWith("5.")) {
                try {
                    // Try to cast to Multiverse-Core 5.x
                    org.mvplugins.multiverse.core.MultiverseCore mvCore = 
                        (org.mvplugins.multiverse.core.MultiverseCore) mvPlugin;
                    
                    MultiverseCore5Adapter adapter = new MultiverseCore5Adapter(mvCore, logger);
                    logger.info("Using world manager adapter: " + adapter.getAdapterName());
                    return adapter;
                } catch (ClassCastException e) {
                    logger.warning("Failed to cast to Multiverse-Core 5.x: " + e.getMessage());
                } catch (Exception e) {
                    logger.warning("Error initializing Multiverse-Core 5.x adapter: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Check if it's version 4.x
            if (version.startsWith("4.")) {
                try {
                    MultiverseCore4Adapter adapter = new MultiverseCore4Adapter(mvPlugin, logger);
                    logger.info("Using world manager adapter: " + adapter.getAdapterName());
                    return adapter;
                } catch (Exception e) {
                    logger.warning("Error initializing Multiverse-Core 4.x adapter: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            logger.warning("Unsupported Multiverse-Core version: " + version);
        }
        
        // Fallback to Bukkit Native
        logger.warning("No world management plugin detected. Using Bukkit native world management.");
        logger.warning("Note: World management functionality will be limited without Multiverse-Core.");
        BukkitNativeAdapter adapter = new BukkitNativeAdapter(logger);
        logger.info("Using world manager adapter: " + adapter.getAdapterName());
        return adapter;
    }
}
