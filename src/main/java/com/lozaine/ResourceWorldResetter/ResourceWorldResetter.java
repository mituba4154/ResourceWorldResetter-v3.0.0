package com.lozaine.ResourceWorldResetter;

import com.lozaine.ResourceWorldResetter.gui.AdminGUI;
import com.lozaine.ResourceWorldResetter.gui.AdminGUIListener;
import com.lozaine.ResourceWorldResetter.lang.LanguageManager;
import com.lozaine.ResourceWorldResetter.utils.LogUtil;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.lozaine.ResourceWorldResetter.metrics.Metrics;
import com.lozaine.ResourceWorldResetter.commands.RwrRegionCommand;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.onarandombox.MultiverseCore.utils.FileUtils.deleteFolder;

public class ResourceWorldResetter extends JavaPlugin {
    private String worldName;
    private MultiverseCore core;
    private int restartTime;
    private int resetWarningTime;
    private String resetType;
    private int resetDay;
    private boolean regionsEnabled;
    private boolean regionsImmediateOnAdd;
    private java.util.Set<String> regionsToReset = new java.util.HashSet<>();
    private AdminGUI adminGUI;
    private int warningTaskId = -1;
    private int resetTaskId = -1;
    private LanguageManager languageManager;

    public String getWorldName() { return this.worldName; }
    public String getResetType() { return this.resetType; }
    public int getRestartTime() { return this.restartTime; }
    public int getResetWarningTime() { return this.resetWarningTime; }
    public int getResetDay() { return this.resetDay; }
    public boolean isRegionsEnabled() { return this.regionsEnabled; }
    public java.util.Set<String> getRegionsToReset() { return java.util.Collections.unmodifiableSet(regionsToReset); }
    public LanguageManager getLanguageManager() { return this.languageManager; }

    public void setWorldName(String name) {
        this.worldName = name;
        getConfig().set("worldName", name);
        saveConfig();
        ensureResourceWorldExists();
    }

    public void setResetType(String type) {
        this.resetType = type;
        getConfig().set("resetType", type);
        saveConfig();
        scheduleDailyReset(); // Reschedule after changing type
    }

    public void setResetDay(int day) {
        this.resetDay = day;
        getConfig().set("resetDay", day);
        saveConfig();
        scheduleDailyReset(); // Reschedule after changing day
    }

    public void setRestartTime(int hour) {
        if (hour >= 0 && hour <= 23) {
            this.restartTime = hour;
            getConfig().set("restartTime", hour);
            saveConfig();
            scheduleDailyReset(); // Reschedule after changing time

            LogUtil.log(getLogger(), "Restart time set to " + hour + ":00", Level.INFO);
        }
    }

    public void setResetWarningTime(int minutes) {
        if (minutes >= 0) {
            this.resetWarningTime = minutes;
            getConfig().set("resetWarningTime", minutes);
            saveConfig();
            scheduleDailyReset(); // Reschedule warning after changing time

            LogUtil.log(getLogger(), "Reset warning time set to " + minutes + " minutes", Level.INFO);
        }
    }

    public void setRegionsEnabled(boolean enabled) {
        this.regionsEnabled = enabled;
        getConfig().set("regions.enabled", enabled);
        saveConfig();
    }

    public void addRegionToReset(int regionX, int regionZ) {
        String key = regionX + "," + regionZ;
        if (regionsToReset.add(key)) {
            java.util.List<String> list = new java.util.ArrayList<>(regionsToReset);
            getConfig().set("regions.list", list);
            saveConfig();
            if (regionsImmediateOnAdd && Bukkit.getWorld(worldName) != null) {
                regenerateRegion(Bukkit.getWorld(worldName), regionX, regionZ);
            }
        }
    }

    public void removeRegionToReset(int regionX, int regionZ) {
        String key = regionX + "," + regionZ;
        if (regionsToReset.remove(key)) {
            java.util.List<String> list = new java.util.ArrayList<>(regionsToReset);
            getConfig().set("regions.list", list);
            saveConfig();
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LogUtil.init(this);
        
        // Load language first
        String language = getConfig().getString("language", "en_us");
        languageManager = new LanguageManager(this, language);
        
        core = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");

        if (core == null) {
            LogUtil.log(getLogger(), "Multiverse-Core not found! Disabling plugin.", Level.SEVERE);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (getConfig().getBoolean("metrics.enabled", true)) {
            int pluginId = 25197;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new Metrics.SimplePie("reset_type", () -> this.resetType));
            metrics.addCustomChart(new Metrics.SimplePie("reset_hour", () -> String.valueOf(this.restartTime)));
            metrics.addCustomChart(new Metrics.SimplePie("warning_time", () -> String.valueOf(this.resetWarningTime)));
            metrics.addCustomChart(new Metrics.SimplePie("server_version", () -> Bukkit.getBukkitVersion()));
            LogUtil.log(getLogger(), "bStats metrics enabled", Level.INFO);
        } else {
            LogUtil.log(getLogger(), "bStats metrics disabled by configuration", Level.INFO);
        }

        loadConfig();
        adminGUI = new AdminGUI(this);
        getServer().getPluginManager().registerEvents(new AdminGUIListener(this, adminGUI), this);
        getCommand("rwrregion").setExecutor(new RwrRegionCommand(this));

        ensureResourceWorldExists();
        scheduleDailyReset();
        LogUtil.log(getLogger(), "ResourcesWorldResetter v" + getDescription().getVersion() + " enabled successfully!", Level.INFO);
    }

    @Override
    public void onDisable() {
        cancelScheduledTasks();
        LogUtil.log(getLogger(), "ResourceWorldResetter disabled.", Level.INFO);
    }

    private void cancelScheduledTasks() {
        if (warningTaskId != -1) {
            Bukkit.getScheduler().cancelTask(warningTaskId);
            warningTaskId = -1;
        }
        if (resetTaskId != -1) {
            Bukkit.getScheduler().cancelTask(resetTaskId);
            resetTaskId = -1;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("resourceworldresetter.admin")) {
            switch (command.getName().toLowerCase()) {
                case "rwrgui":
                    if (sender instanceof Player player) {
                        adminGUI.openMainMenu(player);
                        return true;
                    } else {
                        sender.sendMessage(languageManager.getMessage("message.player_only"));
                        return true;
                    }

                case "reloadrwr":
                    reloadConfig();
                    loadConfig();
                    scheduleDailyReset(); // Re-schedule resets after reload
                    sender.sendMessage(languageManager.getMessage("message.command_reloaded"));
                    return true;

                // Keeping resetworld for backward compatibility
                case "resetworld":
                    sender.sendMessage(languageManager.getMessage("message.command_force_reset"));
                    resetResourceWorld(false);
                    return true;
            }
        } else {
            sender.sendMessage(languageManager.getMessage("message.no_permission"));
            return true;
        }
        return false;
    }

    private void scheduleDailyReset() {
        // Cancel any existing scheduled tasks
        cancelScheduledTasks();

        // For daily, weekly, monthly resets
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReset = now.withHour(restartTime).withMinute(0).withSecond(0);

        // If current time is past reset time, schedule for next occurrence
        if (now.compareTo(nextReset) >= 0) {
            nextReset = nextReset.plusDays(1);
        }

        // Handle weekly resets
        if ("weekly".equals(resetType)) {
            int currentDay = now.getDayOfWeek().getValue(); // 1 (Monday) to 7 (Sunday)
            int daysUntilReset = (resetDay - currentDay + 7) % 7;
            if (daysUntilReset == 0 && now.compareTo(nextReset) >= 0) {
                daysUntilReset = 7;
            }
            nextReset = nextReset.plusDays(daysUntilReset);
            LogUtil.log(getLogger(), "Scheduled weekly reset for " + nextReset, Level.INFO);
        }
        // Handle monthly resets
        else if ("monthly".equals(resetType)) {
            LocalDateTime nextMonth = now;
            if (now.getDayOfMonth() > resetDay || (now.getDayOfMonth() == resetDay && now.compareTo(nextReset) >= 0)) {
                nextMonth = now.plusMonths(1);
            }

            int maxDay = nextMonth.getMonth().length(nextMonth.toLocalDate().isLeapYear());
            int actualResetDay = Math.min(resetDay, maxDay);
            nextReset = nextMonth.withDayOfMonth(actualResetDay).withHour(restartTime).withMinute(0).withSecond(0);
            LogUtil.log(getLogger(), "Scheduled monthly reset for " + nextReset, Level.INFO);
        }
        // Default to daily reset
        else {
            LogUtil.log(getLogger(), "Scheduled daily reset for " + nextReset, Level.INFO);
        }

        // Calculate delay in ticks for reset
        long resetDelayTicks = Math.max(1, ChronoUnit.SECONDS.between(now, nextReset) * 20);

        // Calculate delay for warning (if applicable)
        if (resetWarningTime > 0) {
            LocalDateTime warningTime = nextReset.minusMinutes(resetWarningTime);

            // If warning time is already passed, don't schedule warning
            if (now.isBefore(warningTime)) {
                long warningDelayTicks = Math.max(1, ChronoUnit.SECONDS.between(now, warningTime) * 20);

                LogUtil.log(getLogger(), "Warning scheduled for " + warningTime +
                        " (" + (warningDelayTicks/20/60) + " minutes from now)", Level.INFO);

                warningTaskId = Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.broadcastMessage(languageManager.getMessage("message.reset.warning", 
                            "{minutes}", String.valueOf(resetWarningTime)));
                    LogUtil.log(getLogger(), "Broadcast reset warning to players", Level.INFO);
                }, warningDelayTicks).getTaskId();
            }
        }

        // Schedule the actual reset
        LogUtil.log(getLogger(), "Next reset scheduled in " + (resetDelayTicks/20/60) + " minutes (" +
                (resetDelayTicks/20/60/60) + " hours)", Level.INFO);

        resetTaskId = Bukkit.getScheduler().runTaskLater(this, () -> {
            LogUtil.log(getLogger(), "Executing scheduled reset task", Level.INFO);
            resetResourceWorld(true);
        }, resetDelayTicks).getTaskId();
    }

    public void resetResourceWorld() {
        resetResourceWorld(false);
    }

    public void resetResourceWorld(boolean isScheduled) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            LogUtil.log(getLogger(), "World '" + worldName + "' not found! Attempting to create it...", Level.WARNING);
            ensureResourceWorldExists();
            world = Bukkit.getWorld(worldName);

            if (world == null) {
                LogUtil.log(getLogger(), "Failed to create world '" + worldName + "'! Reset aborted.", Level.SEVERE);
                return;
            }
        }

        // Immediate reset for manual resets or if warning time is 0
        if (!isScheduled || resetWarningTime <= 0) {
            if (regionsEnabled) {
                performRegionReset(world);
            } else {
                performReset(world);
            }
        } else {
            // For scheduled resets, the warning was already handled
            if (regionsEnabled) {
                performRegionReset(world);
            } else {
                performReset(world);
            }
        }
    }

    private void performReset(World world) {
        double tpsBefore = getServerTPS();
        long startTime = System.currentTimeMillis();

        LogUtil.log(getLogger(), "Starting world reset process for " + worldName, Level.INFO);
        Bukkit.broadcastMessage(languageManager.getMessage("message.reset.starting"));

        teleportPlayersSafely(world);

        MVWorldManager worldManager = core.getMVWorldManager();
        if (!worldManager.unloadWorld(worldName)) {
            LogUtil.log(getLogger(), "Failed to unload world: " + worldName + ". Retrying with forced unload.", Level.WARNING);

            // Try forcing world unload if normal unload fails
            if (!worldManager.unloadWorld(worldName, true)) {
                LogUtil.log(getLogger(), "Forced unload also failed. Aborting reset.", Level.SEVERE);
                Bukkit.broadcastMessage(languageManager.getMessage("message.reset.failed"));
                return;
            }
        }

        CompletableFuture.runAsync(() -> {
            File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
            LogUtil.log(getLogger(), "Deleting world folder: " + worldFolder.getAbsolutePath(), Level.INFO);

            if (deleteFolder(worldFolder)) {
                Bukkit.getScheduler().runTask(this, () -> {
                    LogUtil.log(getLogger(), "World folder deleted, recreating world", Level.INFO);
                    recreateWorld(worldManager);
                    long duration = System.currentTimeMillis() - startTime;
                    double tpsAfter = getServerTPS();
                    Bukkit.broadcastMessage(languageManager.getMessage("message.reset.completed",
                            "{seconds}", String.valueOf(duration/1000),
                            "{tps_before}", String.format("%.2f", tpsBefore),
                            "{tps_after}", String.format("%.2f", tpsAfter)));
                    LogUtil.log(getLogger(), "Resource world reset completed in " + duration + "ms", Level.INFO);

                    // Reschedule for next time
                    if (resetTaskId != -1) {
                        Bukkit.getScheduler().runTaskLater(this, this::scheduleDailyReset, 20);
                    }
                });
            } else {
                LogUtil.log(getLogger(), "Failed to delete world folder: " + worldName, Level.SEVERE);
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.broadcastMessage(languageManager.getMessage("message.reset.failed"));
                });
            }
        });
    }

    private void performRegionReset(World world) {
        LogUtil.log(getLogger(), "Starting region-based reset for " + worldName, Level.INFO);
        Bukkit.broadcastMessage(languageManager.getMessage("message.region_reset.starting"));

        // Teleport players out of affected regions to world spawn
        teleportPlayersOutOfRegions(world);

        // Regenerate region files
        for (String key : regionsToReset) {
            String[] parts = key.split(",");
            try {
                int rx = Integer.parseInt(parts[0]);
                int rz = Integer.parseInt(parts[1]);
                regenerateRegion(world, rx, rz);
            } catch (Exception e) {
                LogUtil.log(getLogger(), "Invalid region entry: " + key, Level.WARNING);
            }
        }

        Bukkit.broadcastMessage(languageManager.getMessage("message.region_reset.completed"));
        LogUtil.log(getLogger(), "Region-based reset completed", Level.INFO);
    }

    private void teleportPlayersOutOfRegions(World world) {
        World defaultWorld = Bukkit.getWorlds().get(0);
        org.bukkit.Location spawn = defaultWorld.getSpawnLocation();
        for (Player player : world.getPlayers()) {
            org.bukkit.Location loc = player.getLocation();
            int chunkX = loc.getBlockX() >> 4;
            int chunkZ = loc.getBlockZ() >> 4;
            int regionX = chunkX >> 5; // 32x32 chunks per region
            int regionZ = chunkZ >> 5;
            String key = regionX + "," + regionZ;
            if (regionsToReset.contains(key)) {
                player.teleport(spawn);
                player.sendMessage(languageManager.getMessage("message.region_reset.player_moved"));
            }
        }
    }

    private void regenerateRegion(World world, int regionX, int regionZ) {
        try {
            // Unload chunks in target region
            for (int cx = regionX << 5; cx < (regionX << 5) + 32; cx++) {
                for (int cz = regionZ << 5; cz < (regionZ << 5) + 32; cz++) {
                    if (world.isChunkLoaded(cx, cz)) {
                        world.unloadChunk(cx, cz, true);
                    }
                }
            }

            // Delete region file(s)
            java.io.File regionDir = new java.io.File(world.getWorldFolder(), "region");
            java.io.File regionFile = new java.io.File(regionDir, "r." + regionX + "." + regionZ + ".mca");
            if (regionFile.exists()) {
                LogUtil.log(getLogger(), "Deleting region file: " + regionFile.getAbsolutePath(), Level.INFO);
                if (!regionFile.delete()) {
                    LogUtil.log(getLogger(), "Failed to delete region file: " + regionFile.getName(), Level.WARNING);
                }
            }

            // Force chunks to regenerate on next access by clearing POI and entities via async task
            // Note: Spigot handles regeneration when chunk is generated again.
        } catch (Exception e) {
            LogUtil.log(getLogger(), "Error regenerating region (" + regionX + "," + regionZ + "): " + e.getMessage(), Level.SEVERE);
        }
    }

    public double getServerTPS() {
        try {
            // Attempt Paper API first if available
            java.lang.reflect.Method getTPSMethod = Bukkit.getServer().getClass().getMethod("getTPS");
            Object tpsObj = getTPSMethod.invoke(Bukkit.getServer());
            if (tpsObj instanceof double[] tpsArray && tpsArray.length > 0) {
                return tpsArray[0];
            }
        } catch (NoSuchMethodException ignored) {
            // Fall through to CraftBukkit/NMS reflection
        } catch (Exception e) {
            getLogger().fine("Paper getTPS reflection failed: " + e.getMessage());
        }

        try {
            Object mcServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            double[] recentTps = (double[]) mcServer.getClass().getField("recentTps").get(mcServer);
            return recentTps[0];
        } catch (Exception e) {
            getLogger().warning("Failed to get server TPS. Defaulting to 20.0");
            return 20.0;
        }
    }

    public void teleportPlayersSafely(World world) {
        World defaultWorld = Bukkit.getWorlds().get(0); // Get server's default world
        Location spawn = defaultWorld.getSpawnLocation();

        for (Player player : world.getPlayers()) {
            player.teleport(spawn);
            player.sendMessage(languageManager.getMessage("message.reset.player_teleported"));
            LogUtil.log(getLogger(), "Teleported " + player.getName() + " out of resource world", Level.INFO);
        }
    }

    public void recreateWorld(MVWorldManager worldManager) {
        boolean success = worldManager.addWorld(
                worldName,
                World.Environment.NORMAL,
                null,
                WorldType.NORMAL,
                true,
                "DEFAULT"
        );

        if (success) {
            Bukkit.broadcastMessage(languageManager.getMessage("message.reset.recreated"));
            LogUtil.log(getLogger(), "World recreation successful", Level.INFO);
        } else {
            Bukkit.broadcastMessage(languageManager.getMessage("message.reset.recreate_failed"));
            LogUtil.log(getLogger(), "Failed to recreate world: " + worldName, Level.SEVERE);
        }
    }

    public void ensureResourceWorldExists() {
        MVWorldManager worldManager = core.getMVWorldManager();
        if (!worldManager.isMVWorld(worldName)) {
            LogUtil.log(getLogger(), "Resource world doesn't exist, creating: " + worldName, Level.INFO);
            boolean success = worldManager.addWorld(
                    worldName,
                    World.Environment.NORMAL,
                    null,
                    WorldType.NORMAL,
                    true,
                    "DEFAULT"
            );
            LogUtil.log(getLogger(), "Created resource world: " + worldName + ", Success: " + success, Level.INFO);
        } else {
            LogUtil.log(getLogger(), "Resource world exists: " + worldName, Level.INFO);
        }
    }

    public void loadConfig() {
        reloadConfig();
        worldName = getConfig().getString("worldName", "Resources");
        restartTime = getConfig().getInt("restartTime", 3);
        resetWarningTime = getConfig().getInt("resetWarningTime", 5);
        resetType = getConfig().getString("resetType", "daily");
        resetDay = getConfig().getInt("resetDay", 1);

        regionsEnabled = getConfig().getBoolean("regions.enabled", false);
        regionsImmediateOnAdd = getConfig().getBoolean("regions.immediateRegenerationOnAdd", true);
        regionsToReset.clear();
        java.util.List<String> list = getConfig().getStringList("regions.list");
        if (list != null) regionsToReset.addAll(list);
        
        // Reload language if changed
        String language = getConfig().getString("language", "en_us");
        if (languageManager != null) {
            languageManager.reload(language);
        }

        LogUtil.log(getLogger(), "Configuration loaded: worldName=" + worldName +
                ", resetType=" + resetType + ", restartTime=" + restartTime +
                ", resetWarningTime=" + resetWarningTime, Level.INFO);
    }
}