package com.lozaine.ResourceWorldResetter.gui;

import com.lozaine.ResourceWorldResetter.ResourceWorldResetter;
import com.lozaine.ResourceWorldResetter.lang.LanguageManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AdminGUI implements Listener {
    private final ResourceWorldResetter plugin;
    private final LanguageManager lang;
    private final Map<UUID, GuiType> activeGuis = new HashMap<>();
    private final MultiverseCore mvCore;

    public enum GuiType {
        MAIN_MENU,
        RESET_TYPE_MENU,
        RESET_DAY_MENU,
        WARNING_TIME_MENU,
        RESTART_TIME_MENU,
        MONTHLY_DAY_MENU,
        WORLD_SELECTION_MENU
    }

    public AdminGUI(ResourceWorldResetter plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
        this.mvCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public GuiType getActiveGuiType(UUID playerId) {
        return activeGuis.get(playerId);
    }

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, lang.getMessage("gui.title.main"));

        // Current settings display with colors
        gui.setItem(4, createInfoItem(Material.BOOK, lang.getMessage("gui.main.current_settings"),
                lang.getMessage("gui.main.world", "{world}", plugin.getWorldName()),
                lang.getMessage("gui.main.reset_type", "{type}", capitalizeFirstLetter(plugin.getResetType())),
                lang.getMessage("gui.main.restart_time", "{time}", String.valueOf(plugin.getRestartTime())),
                lang.getMessage("gui.main.warning_time", "{minutes}", String.valueOf(plugin.getResetWarningTime()))));

        // Main options with improved icons and descriptions
        gui.setItem(10, createGuiItem(Material.GRASS_BLOCK, lang.getMessage("gui.main.change_world"), 
                lang.getMessage("gui.main.change_world_desc")));
        gui.setItem(12, createGuiItem(Material.CLOCK, lang.getMessage("gui.main.reset_type_menu"), 
                lang.getMessage("gui.main.reset_type_desc")));
        gui.setItem(14, createGuiItem(Material.SUNFLOWER, lang.getMessage("gui.main.restart_time_menu"), 
                lang.getMessage("gui.main.restart_time_desc")));
        gui.setItem(16, createGuiItem(Material.BELL, lang.getMessage("gui.main.warning_time_menu"), 
                lang.getMessage("gui.main.warning_time_desc")));

        String regionModeText = plugin.isRegionsEnabled() ? 
                lang.getMessage("gui.main.disable_region_mode") : 
                lang.getMessage("gui.main.enable_region_mode");
        gui.setItem(18, createGuiItem(Material.FILLED_MAP, regionModeText, 
                lang.getMessage("gui.main.region_mode_desc")));
        gui.setItem(20, createGuiItem(Material.MAP, lang.getMessage("gui.main.manage_regions"), 
                lang.getMessage("gui.main.manage_regions_desc")));
        gui.setItem(22, createGuiItem(Material.TNT, lang.getMessage("gui.main.force_reset"), 
                lang.getMessage("gui.main.force_reset_desc")));
        gui.setItem(24, createGuiItem(Material.REDSTONE, lang.getMessage("gui.main.reload_config"), 
                lang.getMessage("gui.main.reload_config_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.MAIN_MENU);
    }

    // World selection menu
    public void openWorldSelectionMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, lang.getMessage("gui.title.world_selection"));

        int slot = 0;

        // Get all worlds from Multiverse
        if (mvCore != null) {
            Collection<MultiverseWorld> mvWorlds = mvCore.getMVWorldManager().getMVWorlds();
            for (MultiverseWorld mvWorld : mvWorlds) {
                World world = mvWorld.getCBWorld();
                if (world != null) {
                    Material icon = Material.GRASS_BLOCK;
                    String description = lang.getMessage("gui.world.normal");

                    // Choose appropriate icon based on world type
                    if (world.getEnvironment() == World.Environment.NETHER) {
                        icon = Material.NETHERRACK;
                        description = lang.getMessage("gui.world.nether");
                    } else if (world.getEnvironment() == World.Environment.THE_END) {
                        icon = Material.END_STONE;
                        description = lang.getMessage("gui.world.end");
                    }

                    // If this is the current resource world, highlight it
                    String worldName = world.getName();
                    String displayName = worldName;
                    if (worldName.equals(plugin.getWorldName())) {
                        displayName = lang.getMessage("gui.world.current", "{world}", worldName);
                    }

                    gui.setItem(slot++, createGuiItem(icon, displayName, description));

                    // Ensure we don't exceed inventory size
                    if (slot >= 45) break;
                }
            }
        } else {
            // Fallback if Multiverse isn't available
            for (World world : Bukkit.getWorlds()) {
                Material icon = Material.GRASS_BLOCK;
                String description = lang.getMessage("gui.world.normal");

                if (world.getEnvironment() == World.Environment.NETHER) {
                    icon = Material.NETHERRACK;
                    description = lang.getMessage("gui.world.nether");
                } else if (world.getEnvironment() == World.Environment.THE_END) {
                    icon = Material.END_STONE;
                    description = lang.getMessage("gui.world.end");
                }

                String worldName = world.getName();
                String displayName = worldName;
                if (worldName.equals(plugin.getWorldName())) {
                    displayName = lang.getMessage("gui.world.current", "{world}", worldName);
                }

                gui.setItem(slot++, createGuiItem(icon, displayName, description));

                if (slot >= 45) break;
            }
        }

        // Back button
        gui.setItem(49, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.WORLD_SELECTION_MENU);
    }

    // Helper method to capitalize first letter
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createInfoItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + name);
            meta.setLore(Arrays.asList(Arrays.stream(lore).map(s -> ChatColor.GRAY + s).toArray(String[]::new)));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openResetTypeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, lang.getMessage("gui.title.reset_type"));

        gui.setItem(2, createGuiItem(Material.PAPER, lang.getMessage("gui.reset_type.daily"), 
                lang.getMessage("gui.reset_type.daily_desc")));
        gui.setItem(4, createGuiItem(Material.BOOK, lang.getMessage("gui.reset_type.weekly"), 
                lang.getMessage("gui.reset_type.weekly_desc")));
        gui.setItem(6, createGuiItem(Material.CLOCK, lang.getMessage("gui.reset_type.monthly"), 
                lang.getMessage("gui.reset_type.monthly_desc")));

        gui.setItem(8, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESET_TYPE_MENU);
    }

    public void openResetDayMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, lang.getMessage("gui.title.reset_day"));

        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (int i = 0; i < 7; i++) {
            String dayName = lang.getMessage("gui.reset_day." + dayKeys[i]);
            String desc = lang.getMessage("gui.reset_day.desc", "{day}", dayName);
            gui.setItem(i, createGuiItem(Material.PAPER, dayName, desc));
        }
        gui.setItem(8, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESET_DAY_MENU);
    }

    public void openWarningTimeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 18, lang.getMessage("gui.title.warning_time"));

        // Common warning times
        gui.setItem(0, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.no_warning"), 
                lang.getMessage("gui.warning_time.no_warning_desc")));
        gui.setItem(1, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.one_minute"), 
                lang.getMessage("gui.warning_time.one_minute_desc")));
        gui.setItem(2, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.five_minutes"), 
                lang.getMessage("gui.warning_time.five_minutes_desc")));
        gui.setItem(3, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.ten_minutes"), 
                lang.getMessage("gui.warning_time.ten_minutes_desc")));
        gui.setItem(4, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.fifteen_minutes"), 
                lang.getMessage("gui.warning_time.fifteen_minutes_desc")));
        gui.setItem(5, createGuiItem(Material.CLOCK, lang.getMessage("gui.warning_time.thirty_minutes"), 
                lang.getMessage("gui.warning_time.thirty_minutes_desc")));

        gui.setItem(17, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.WARNING_TIME_MENU);
    }

    public void openRestartTimeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, lang.getMessage("gui.title.restart_time"));

        // Create slots for each hour (0-23)
        for (int hour = 0; hour < 24; hour++) {
            String hourDisplay = lang.getMessage("gui.restart_time.hour", "{hour}", String.valueOf(hour));
            String ampm = (hour < 12) ? "AM" : "PM";
            int displayHour = (hour == 0 || hour == 12) ? 12 : hour % 12;
            String description = lang.getMessage("gui.restart_time.desc", 
                    "{display_hour}", String.valueOf(displayHour), "{ampm}", ampm);

            gui.setItem(hour, createGuiItem(Material.CLOCK, hourDisplay, description));
        }

        gui.setItem(26, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESTART_TIME_MENU);
    }

    // Open monthly day selection menu
    public void openMonthlyDayMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 36, lang.getMessage("gui.title.monthly_day"));

        // Days 1-31
        for (int day = 1; day <= 31; day++) {
            String dayText = lang.getMessage("gui.monthly_day.day", "{day}", String.valueOf(day));
            String desc = lang.getMessage("gui.monthly_day.desc", "{day}", String.valueOf(day));
            gui.setItem(day - 1, createGuiItem(Material.PAPER, dayText, desc));
        }

        gui.setItem(35, createGuiItem(Material.BARRIER, lang.getMessage("gui.common.back"), 
                lang.getMessage("gui.common.back_desc")));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.MONTHLY_DAY_MENU);
    }

    public boolean hasActiveGui(UUID playerUuid) {
        return activeGuis.containsKey(playerUuid);
    }

    public void removeActiveGui(UUID playerUuid) {
        activeGuis.remove(playerUuid);
    }
}