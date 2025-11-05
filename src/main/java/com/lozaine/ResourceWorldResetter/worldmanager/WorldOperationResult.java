package com.lozaine.ResourceWorldResetter.worldmanager;

/**
 * Result of a world operation (create, load, unload)
 */
public class WorldOperationResult {
    private final boolean success;
    private final String message;
    
    private WorldOperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static WorldOperationResult success() {
        return new WorldOperationResult(true, "Success");
    }
    
    public static WorldOperationResult success(String message) {
        return new WorldOperationResult(true, message);
    }
    
    public static WorldOperationResult failure(String message) {
        return new WorldOperationResult(false, message);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isFailure() {
        return !success;
    }
    
    public String getMessage() {
        return message;
    }
}
