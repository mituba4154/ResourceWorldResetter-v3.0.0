# Multiverse-Core 5.x Compatibility Update

## Summary
This update makes ResourceWorldResetter v3.0.0 fully compatible with Multiverse-Core 5.x (including 5.2.1) while maintaining backward compatibility with older versions.

## Problem
The original plugin failed to load with Multiverse-Core 5.x with the error:
```
java.lang.NoClassDefFoundError: com/onarandombox/MultiverseCore/MultiverseCore
```

This occurred because:
1. The plugin had a hard dependency on Multiverse-Core in plugin.yml
2. Direct imports of MultiverseCore classes caused ClassNotFoundException at load time
3. The plugin was built against Multiverse-Core 4.3.1

## Solution
The plugin now uses reflection to interact with Multiverse-Core API, making it compatible with both version 4.x and 5.x.

### Changes Made

#### 1. plugin.yml
- Changed from `depend: [Multiverse-Core]` to `softdepend: [Multiverse-Core]`
- Plugin now loads even if Multiverse-Core is not installed (with limited functionality)

#### 2. build.gradle
- Updated dependency from `Multiverse-Core:4.3.1` to `Multiverse-Core:5.2.1`
- Updated Spigot API from `1.21.5-R0.1-SNAPSHOT` to `1.21.4-R0.1-SNAPSHOT`

#### 3. ResourceWorldResetter.java
**Removed direct imports:**
- ~~`import com.onarandombox.MultiverseCore.MultiverseCore;`~~
- ~~`import com.onarandombox.MultiverseCore.api.MVWorldManager;`~~
- ~~`import static com.onarandombox.MultiverseCore.utils.FileUtils.deleteFolder;`~~

**Changed field type:**
- Changed `MultiverseCore core` to `Object multiverseCore`

**Added reflection-based helper methods:**
- `hasMultiverseCore()` - Check if MV-Core is available
- `getMVWorldManager()` - Get world manager via reflection
- `isMVWorld(String)` - Check if world is MV world
- `addMVWorld(...)` - Add world using MV API
- `unloadMVWorld(String)` - Unload world via reflection
- `unloadMVWorld(String, boolean)` - Force unload world
- `deleteFolder(File)` - Own implementation of folder deletion

**Updated onEnable():**
- No longer disables plugin if MV-Core is missing
- Uses Plugin API instead of direct cast
- Logs warning instead of error when MV-Core unavailable
- Added MV version to bStats metrics

**Updated world management methods:**
- `ensureResourceWorldExists()` - Falls back to Bukkit API when MV unavailable
- `performReset()` - Uses reflection helpers
- `recreateWorld()` - Simplified to use helper methods

#### 4. AdminGUI.java
**Removed direct imports:**
- ~~`import com.onarandombox.MultiverseCore.MultiverseCore;`~~
- ~~`import com.onarandombox.MultiverseCore.api.MultiverseWorld;`~~

**Updated constructor:**
- Removed direct MV-Core field and initialization

**Updated openWorldSelectionMenu():**
- Uses reflection to access MV worlds
- Falls back to Bukkit.getWorlds() if reflection fails or MV unavailable
- Maintains all functionality

## Behavior Changes

### When Multiverse-Core is installed (v4.x or v5.x):
- Plugin works exactly as before
- Uses MV API for world management
- Full feature support

### When Multiverse-Core is not installed:
- Plugin loads successfully (doesn't crash)
- Logs warning about limited functionality
- Falls back to Bukkit API for basic world operations
- Some features may not work (automatic world creation via MV)

## Testing
To test compatibility:

1. **With Multiverse-Core 5.2.1:**
   - Install Multiverse-Core 5.2.1
   - Install ResourceWorldResetter
   - Plugin should load without errors
   - Check logs for: "Multiverse-Core 5.2.1 found and integrated!"

2. **With Multiverse-Core 4.x:**
   - Plugin should still work via reflection
   - Check logs for successful integration

3. **Without Multiverse-Core:**
   - Plugin should load with warning
   - Check logs for: "Multiverse-Core not found! Plugin will have limited functionality."

## Compatibility Matrix
| Multiverse-Core Version | Status |
|------------------------|--------|
| Not installed          | ✅ Loads with warnings |
| 4.x (4.3.1+)          | ✅ Full compatibility via reflection |
| 5.x (5.2.1)           | ✅ Full compatibility via reflection |

## Technical Notes
- Reflection adds minimal performance overhead (only during initialization and world operations)
- All MV API calls are wrapped in try-catch blocks for robustness
- The plugin is now more resilient to API changes in future MV versions
- No breaking changes to existing functionality

## Migration
No migration needed. Simply replace the old JAR with the new one.
