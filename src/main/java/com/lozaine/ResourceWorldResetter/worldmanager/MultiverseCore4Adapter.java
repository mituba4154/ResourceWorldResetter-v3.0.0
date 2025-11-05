package com.lozaine.ResourceWorldResetter.worldmanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Adapter for Multiverse-Core 4.x using reflection for compatibility
 */
public class MultiverseCore4Adapter implements WorldManagerAdapter {
    private final Object multiverseCore;
    private final Object mvWorldManager;
    private final Logger logger;
    private final String version;
    
    public MultiverseCore4Adapter(Object multiverseCore, Logger logger) throws ReflectiveOperationException {
        this.multiverseCore = multiverseCore;
        this.logger = logger;
        
        // Get version
        Method getDescriptionMethod = multiverseCore.getClass().getMethod("getDescription");
        Object pluginDescriptor = getDescriptionMethod.invoke(multiverseCore);
        Method getVersionMethod = pluginDescriptor.getClass().getMethod("getVersion");
        this.version = (String) getVersionMethod.invoke(pluginDescriptor);
        
        // Get MVWorldManager using reflection
        Method getMVWorldManagerMethod = multiverseCore.getClass().getMethod("getMVWorldManager");
        this.mvWorldManager = getMVWorldManagerMethod.invoke(multiverseCore);
        
        if (mvWorldManager == null) {
            throw new IllegalStateException("Failed to get MVWorldManager from Multiverse-Core 4.x");
        }
        
        logger.info("Initialized Multiverse-Core 4.x adapter (version: " + version + ")");
    }
    
    @Override
    public String getAdapterName() {
        return "Multiverse-Core 4.x (v" + version + ")";
    }
    
    @Override
    public boolean worldExists(String worldName) {
        try {
            Method isMVWorldMethod = mvWorldManager.getClass().getMethod("isMVWorld", String.class);
            return (Boolean) isMVWorldMethod.invoke(mvWorldManager, worldName);
        } catch (Exception e) {
            logger.warning("Error checking if world exists: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isWorldLoaded(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
    
    @Override
    public WorldOperationResult unloadWorld(String worldName) {
        try {
            // Get the Bukkit world to save it first
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return WorldOperationResult.failure("World is not loaded: " + worldName);
            }
            
            // Save the world before unloading
            logger.info("Saving world before unload: " + worldName);
            world.save();
            
            // Force unload all chunks
            logger.info("Unloading chunks for world: " + worldName);
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                chunk.unload(true);
            }
            
            // Wait a tick for chunks to fully unload
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Try to unload using Multiverse API
            logger.info("Attempting to unload world using Multiverse-Core 4.x: " + worldName);
            Method unloadWorldMethod = mvWorldManager.getClass().getMethod("unloadWorld", String.class);
            Boolean result = (Boolean) unloadWorldMethod.invoke(mvWorldManager, worldName);
            
            if (result != null && result) {
                logger.info("Successfully unloaded world: " + worldName);
                return WorldOperationResult.success("World unloaded successfully");
            } else {
                String errorMsg = "Failed to unload world: " + worldName;
                logger.severe(errorMsg);
                
                // Additional diagnostics
                world = Bukkit.getWorld(worldName);
                if (world != null) {
                    int playerCount = world.getPlayers().size();
                    int chunkCount = world.getLoadedChunks().length;
                    logger.severe("Diagnostics - Players in world: " + playerCount + ", Loaded chunks: " + chunkCount);
                    
                    if (playerCount > 0) {
                        logger.severe("Cannot unload world - players are still in the world!");
                    }
                }
                
                return WorldOperationResult.failure(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error unloading world: " + worldName + " - " + e.getMessage();
            logger.severe(errorMsg);
            e.printStackTrace();
            return WorldOperationResult.failure(errorMsg);
        }
    }
    
    @Override
    public WorldOperationResult createWorld(String worldName, World.Environment environment, WorldType worldType, boolean generateStructures) {
        try {
            logger.info("Creating world with Multiverse-Core 4.x: " + worldName);
            
            // addWorld(String name, Environment env, String seedString, String generator, Boolean structs, String gameMode)
            Method addWorldMethod = mvWorldManager.getClass().getMethod(
                "addWorld", String.class, World.Environment.class, String.class, String.class, Boolean.class, String.class);
            
            Boolean result = (Boolean) addWorldMethod.invoke(
                mvWorldManager, worldName, environment, null, null, generateStructures, null);
            
            if (result != null && result) {
                logger.info("Successfully created world: " + worldName);
                return WorldOperationResult.success("World created successfully");
            } else {
                String errorMsg = "Failed to create world: " + worldName;
                logger.severe(errorMsg);
                return WorldOperationResult.failure(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error creating world: " + worldName + " - " + e.getMessage();
            logger.severe(errorMsg);
            e.printStackTrace();
            return WorldOperationResult.failure(errorMsg);
        }
    }
    
    @Override
    public WorldOperationResult loadWorld(String worldName) {
        try {
            // Check if world is already loaded
            if (Bukkit.getWorld(worldName) != null) {
                return WorldOperationResult.success("World is already loaded");
            }
            
            logger.info("Loading world with Multiverse-Core 4.x: " + worldName);
            
            Method loadWorldMethod = mvWorldManager.getClass().getMethod("loadWorld", String.class);
            Boolean result = (Boolean) loadWorldMethod.invoke(mvWorldManager, worldName);
            
            if (result != null && result) {
                logger.info("Successfully loaded world: " + worldName);
                return WorldOperationResult.success("World loaded successfully");
            } else {
                String errorMsg = "Failed to load world: " + worldName;
                logger.severe(errorMsg);
                return WorldOperationResult.failure(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error loading world: " + worldName + " - " + e.getMessage();
            logger.severe(errorMsg);
            e.printStackTrace();
            return WorldOperationResult.failure(errorMsg);
        }
    }
}
