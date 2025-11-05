package com.lozaine.ResourceWorldResetter.worldmanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.logging.Logger;

/**
 * Native Bukkit world manager adapter (fallback when no world management plugin is available)
 */
public class BukkitNativeAdapter implements WorldManagerAdapter {
    private final Logger logger;
    
    public BukkitNativeAdapter(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public String getAdapterName() {
        return "Bukkit Native (No world management plugin)";
    }
    
    @Override
    public boolean worldExists(String worldName) {
        // Check if world folder exists
        java.io.File worldFolder = new java.io.File(Bukkit.getWorldContainer(), worldName);
        return worldFolder.exists() && worldFolder.isDirectory();
    }
    
    @Override
    public boolean isWorldLoaded(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
    
    @Override
    public WorldOperationResult unloadWorld(String worldName) {
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
        
        // Attempt to unload world
        logger.info("Attempting to unload world using Bukkit native: " + worldName);
        boolean result = Bukkit.unloadWorld(world, true);
        
        if (result) {
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
    }
    
    @Override
    public WorldOperationResult createWorld(String worldName, World.Environment environment, WorldType worldType, boolean generateStructures) {
        logger.info("Creating world with Bukkit native: " + worldName);
        
        WorldCreator creator = new WorldCreator(worldName);
        creator.environment(environment);
        creator.type(worldType);
        creator.generateStructures(generateStructures);
        
        try {
            World world = creator.createWorld();
            if (world != null) {
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
        // Check if world is already loaded
        if (Bukkit.getWorld(worldName) != null) {
            return WorldOperationResult.success("World is already loaded");
        }
        
        // Check if world exists
        if (!worldExists(worldName)) {
            return WorldOperationResult.failure("World folder does not exist: " + worldName);
        }
        
        logger.info("Loading world with Bukkit native: " + worldName);
        
        try {
            WorldCreator creator = new WorldCreator(worldName);
            World world = creator.createWorld();
            
            if (world != null) {
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
