package com.lozaine.ResourceWorldResetter.worldmanager;

import org.bukkit.World;
import org.bukkit.WorldType;

/**
 * Interface for abstracting world management operations across different plugins
 */
public interface WorldManagerAdapter {
    
    /**
     * Get the name of this adapter for logging purposes
     * @return The adapter name (e.g., "Multiverse-Core 5.x", "Bukkit Native")
     */
    String getAdapterName();
    
    /**
     * Check if a world exists (loaded or unloaded)
     * @param worldName The name of the world
     * @return true if the world exists
     */
    boolean worldExists(String worldName);
    
    /**
     * Check if a world is currently loaded
     * @param worldName The name of the world
     * @return true if the world is loaded
     */
    boolean isWorldLoaded(String worldName);
    
    /**
     * Unload a world
     * @param worldName The name of the world to unload
     * @return WorldOperationResult indicating success or failure
     */
    WorldOperationResult unloadWorld(String worldName);
    
    /**
     * Create a new world
     * @param worldName The name of the world to create
     * @param environment The world environment (NORMAL, NETHER, THE_END)
     * @param worldType The world type
     * @param generateStructures Whether to generate structures
     * @return WorldOperationResult indicating success or failure
     */
    WorldOperationResult createWorld(String worldName, World.Environment environment, WorldType worldType, boolean generateStructures);
    
    /**
     * Load an existing world
     * @param worldName The name of the world to load
     * @return WorldOperationResult indicating success or failure
     */
    WorldOperationResult loadWorld(String worldName);
}
