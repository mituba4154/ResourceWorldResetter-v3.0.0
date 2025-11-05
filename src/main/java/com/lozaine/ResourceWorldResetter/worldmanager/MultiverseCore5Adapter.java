package com.lozaine.ResourceWorldResetter.worldmanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapter for Multiverse-Core 5.x
 */
public class MultiverseCore5Adapter implements WorldManagerAdapter {
    private final MultiverseCore core;
    private final Logger logger;
    
    public MultiverseCore5Adapter(MultiverseCore core, Logger logger) {
        this.core = core;
        this.logger = logger;
    }
    
    @Override
    public String getAdapterName() {
        return "Multiverse-Core 5.x (v" + core.getDescription().getVersion() + ")";
    }
    
    @Override
    public boolean worldExists(String worldName) {
        WorldManager worldManager = core.getApi().getWorldManager();
        return worldManager.isWorld(worldName);
    }
    
    @Override
    public boolean isWorldLoaded(String worldName) {
        WorldManager worldManager = core.getApi().getWorldManager();
        return worldManager.getLoadedWorld(worldName).isDefined();
    }
    
    @Override
    public WorldOperationResult unloadWorld(String worldName) {
        WorldManager worldManager = core.getApi().getWorldManager();
        
        // First check if world is loaded
        var loadedWorldOpt = worldManager.getLoadedWorld(worldName);
        if (!loadedWorldOpt.isDefined()) {
            return WorldOperationResult.failure("World is not loaded: " + worldName);
        }
        
        // Get the Bukkit world to save it first
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            // Save the world before unloading to prevent data loss
            logger.info("Saving world before unload: " + worldName);
            world.save();
            
            // Force unload all chunks
            logger.info("Unloading chunks for world: " + worldName);
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                chunk.unload(true);
            }
        }
        
        // Create unload options
        org.mvplugins.multiverse.core.world.options.UnloadWorldOptions unloadOptions = 
            org.mvplugins.multiverse.core.world.options.UnloadWorldOptions.world(loadedWorldOpt.get())
                .saveBukkitWorld(true);
        
        logger.info("Attempting to unload world using Multiverse-Core 5.x: " + worldName);
        var unloadResult = worldManager.unloadWorld(unloadOptions);
        
        if (unloadResult.isSuccess()) {
            logger.info("Successfully unloaded world: " + worldName);
            return WorldOperationResult.success("World unloaded successfully");
        } else {
            String errorMsg = "Failed to unload world: " + worldName + ". Error: " + unloadResult.getFailureReason();
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
    }
    
    @Override
    public WorldOperationResult createWorld(String worldName, World.Environment environment, WorldType worldType, boolean generateStructures) {
        WorldManager worldManager = core.getApi().getWorldManager();
        
        logger.info("Creating world with Multiverse-Core 5.x: " + worldName);
        
        org.mvplugins.multiverse.core.world.options.CreateWorldOptions createOptions =
            org.mvplugins.multiverse.core.world.options.CreateWorldOptions.worldName(worldName)
                .environment(environment)
                .worldType(worldType)
                .generateStructures(generateStructures);
        
        var createResult = worldManager.createWorld(createOptions);
        
        if (createResult.isSuccess()) {
            logger.info("Successfully created world: " + worldName);
            return WorldOperationResult.success("World created successfully");
        } else {
            String errorMsg = "Failed to create world: " + worldName + ". Error: " + createResult.getFailureReason();
            logger.severe(errorMsg);
            return WorldOperationResult.failure(errorMsg);
        }
    }
    
    @Override
    public WorldOperationResult loadWorld(String worldName) {
        WorldManager worldManager = core.getApi().getWorldManager();
        
        // Check if world is already loaded
        if (worldManager.getLoadedWorld(worldName).isDefined()) {
            return WorldOperationResult.success("World is already loaded");
        }
        
        // Get the MultiverseWorld first
        var mvWorldOpt = worldManager.getWorld(worldName);
        if (!mvWorldOpt.isDefined()) {
            return WorldOperationResult.failure("World does not exist in Multiverse: " + worldName);
        }
        
        logger.info("Loading world with Multiverse-Core 5.x: " + worldName);
        
        org.mvplugins.multiverse.core.world.options.LoadWorldOptions loadOptions =
            org.mvplugins.multiverse.core.world.options.LoadWorldOptions.world(mvWorldOpt.get());
        
        var loadResult = worldManager.loadWorld(loadOptions);
        
        if (loadResult.isSuccess()) {
            logger.info("Successfully loaded world: " + worldName);
            return WorldOperationResult.success("World loaded successfully");
        } else {
            String errorMsg = "Failed to load world: " + worldName + ". Error: " + loadResult.getFailureReason();
            logger.severe(errorMsg);
            return WorldOperationResult.failure(errorMsg);
        }
    }
}
