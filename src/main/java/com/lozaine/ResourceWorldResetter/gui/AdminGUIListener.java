package com.lozaine.ResourceWorldResetter.gui;

import com.lozaine.ResourceWorldResetter.lang.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import com.lozaine.ResourceWorldResetter.ResourceWorldResetter;
import com.lozaine.ResourceWorldResetter.gui.AdminGUI.GuiType;

public class AdminGUIListener implements Listener {
    private final ResourceWorldResetter plugin;
    private final AdminGUI adminGUI;
    private final LanguageManager lang;

    public AdminGUIListener(ResourceWorldResetter plugin, AdminGUI adminGUI) {
        this.plugin = plugin;
        this.adminGUI = adminGUI;
        this.lang = plugin.getLanguageManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (!adminGUI.hasActiveGui(player.getUniqueId())) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || clickedItem.getType() == Material.AIR) return;

        String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        GuiType guiType = adminGUI.getActiveGuiType(player.getUniqueId());

        switch (guiType) {
            case MAIN_MENU:
                handleMainMenuClick(player, itemName);
                break;
            case RESET_TYPE_MENU:
                handleResetTypeMenuClick(player, itemName);
                break;
            case RESET_DAY_MENU:
                handleResetDayMenuClick(player, itemName);
                break;
            case WARNING_TIME_MENU:
                handleWarningTimeMenuClick(player, itemName);
                break;
            case RESTART_TIME_MENU:
                handleRestartTimeMenuClick(player, itemName);
                break;
            case MONTHLY_DAY_MENU:
                handleMonthlyDayMenuClick(player, itemName);
                break;
            case WORLD_SELECTION_MENU:
                handleWorldSelectionMenuClick(player, itemName);
                break;
        }
    }

    // New method to handle world selection
    private void handleWorldSelectionMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
            return;
        }

        // Extract the world name (remove color codes and (Current) suffix if present)
        String worldName = itemName;
        if (worldName.contains(" (")) {
            worldName = worldName.substring(0, worldName.indexOf(" ("));
        }

        // Update the world name in plugin config
        plugin.setWorldName(worldName);
        player.sendMessage(lang.getMessage("message.world_set", "{world}", worldName));
        adminGUI.openMainMenu(player);
    }

    private void handleMainMenuClick(Player player, String itemName) {
        String enableRegion = ChatColor.stripColor(lang.getMessage("gui.main.enable_region_mode"));
        String disableRegion = ChatColor.stripColor(lang.getMessage("gui.main.disable_region_mode"));
        String manageRegions = ChatColor.stripColor(lang.getMessage("gui.main.manage_regions"));
        String changeWorld = ChatColor.stripColor(lang.getMessage("gui.main.change_world"));
        String resetType = ChatColor.stripColor(lang.getMessage("gui.main.reset_type_menu"));
        String restartTime = ChatColor.stripColor(lang.getMessage("gui.main.restart_time_menu"));
        String warningTime = ChatColor.stripColor(lang.getMessage("gui.main.warning_time_menu"));
        String forceReset = ChatColor.stripColor(lang.getMessage("gui.main.force_reset"));
        String reloadConfig = ChatColor.stripColor(lang.getMessage("gui.main.reload_config"));

        if (itemName.equals(enableRegion)) {
            plugin.setRegionsEnabled(true);
            player.sendMessage(lang.getMessage("message.region_mode_enabled"));
            adminGUI.openMainMenu(player);
        } else if (itemName.equals(disableRegion)) {
            plugin.setRegionsEnabled(false);
            player.sendMessage(lang.getMessage("message.region_mode_disabled"));
            adminGUI.openMainMenu(player);
        } else if (itemName.equals(manageRegions)) {
            player.sendMessage(lang.getMessage("message.manage_regions_command"));
        } else if (itemName.equals(changeWorld)) {
            adminGUI.openWorldSelectionMenu(player);
        } else if (itemName.equals(resetType)) {
            adminGUI.openResetTypeMenu(player);
        } else if (itemName.equals(restartTime)) {
            adminGUI.openRestartTimeMenu(player);
        } else if (itemName.equals(warningTime)) {
            adminGUI.openWarningTimeMenu(player);
        } else if (itemName.equals(forceReset)) {
            player.closeInventory();
            plugin.resetResourceWorld();
            player.sendMessage(lang.getMessage("message.world_reset_initiated"));
        } else if (itemName.equals(reloadConfig)) {
            player.closeInventory();
            plugin.reloadConfig();
            plugin.loadConfig();
            player.sendMessage(lang.getMessage("message.config_reloaded"));
        }
    }

    private void handleWarningTimeMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
            return;
        }

        String noWarning = ChatColor.stripColor(lang.getMessage("gui.warning_time.no_warning"));
        String oneMinute = ChatColor.stripColor(lang.getMessage("gui.warning_time.one_minute"));
        String fiveMinutes = ChatColor.stripColor(lang.getMessage("gui.warning_time.five_minutes"));
        String tenMinutes = ChatColor.stripColor(lang.getMessage("gui.warning_time.ten_minutes"));
        String fifteenMinutes = ChatColor.stripColor(lang.getMessage("gui.warning_time.fifteen_minutes"));
        String thirtyMinutes = ChatColor.stripColor(lang.getMessage("gui.warning_time.thirty_minutes"));

        int minutes = -1;
        if (itemName.equals(noWarning)) minutes = 0;
        else if (itemName.equals(oneMinute)) minutes = 1;
        else if (itemName.equals(fiveMinutes)) minutes = 5;
        else if (itemName.equals(tenMinutes)) minutes = 10;
        else if (itemName.equals(fifteenMinutes)) minutes = 15;
        else if (itemName.equals(thirtyMinutes)) minutes = 30;

        if (minutes != -1) {
            plugin.setResetWarningTime(minutes);
            player.sendMessage(lang.getMessage("message.warning_time_set", "{minutes}", String.valueOf(minutes)));
        }
        adminGUI.openMainMenu(player);
    }

    private void handleRestartTimeMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
            return;
        }

        try {
            int hour = Integer.parseInt(itemName.split(":")[0]);
            if (hour >= 0 && hour <= 23) {
                plugin.setRestartTime(hour);
                player.sendMessage(lang.getMessage("message.restart_time_set", "{hour}", String.valueOf(hour)));
            }
        } catch (NumberFormatException ignored) {}

        adminGUI.openMainMenu(player);
    }

    private void handleResetTypeMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        String dailyText = ChatColor.stripColor(lang.getMessage("gui.reset_type.daily"));
        String weeklyText = ChatColor.stripColor(lang.getMessage("gui.reset_type.weekly"));
        String monthlyText = ChatColor.stripColor(lang.getMessage("gui.reset_type.monthly"));

        if (itemName.equals(dailyText)) {
            plugin.setResetType("daily");
            player.sendMessage(lang.getMessage("message.reset_type_daily"));
            adminGUI.openMainMenu(player);
        } else if (itemName.equals(weeklyText)) {
            plugin.setResetType("weekly");
            player.sendMessage(lang.getMessage("message.reset_type_weekly"));
            adminGUI.openResetDayMenu(player);
        } else if (itemName.equals(monthlyText)) {
            plugin.setResetType("monthly");
            player.sendMessage(lang.getMessage("message.reset_type_monthly"));
            adminGUI.openMonthlyDayMenu(player);
        } else if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
        }
    }

    private void handleMonthlyDayMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
            return;
        }

        // Try to extract day number from the item name
        try {
            // Remove any non-digit characters and parse
            String numStr = itemName.replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                int day = Integer.parseInt(numStr);
                if (day >= 1 && day <= 31) {
                    plugin.setResetDay(day);
                    player.sendMessage(lang.getMessage("message.monthly_reset_day_set", "{day}", String.valueOf(day)));
                }
            }
        } catch (NumberFormatException ignored) {}

        adminGUI.openMainMenu(player);
    }

    private void handleResetDayMenuClick(Player player, String itemName) {
        String backText = ChatColor.stripColor(lang.getMessage("gui.common.back"));
        if (itemName.equals(backText)) {
            adminGUI.openMainMenu(player);
            return;
        }

        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        int day = -1;
        for (int i = 0; i < dayKeys.length; i++) {
            String dayText = ChatColor.stripColor(lang.getMessage("gui.reset_day." + dayKeys[i]));
            if (itemName.equals(dayText)) {
                day = i + 1;
                break;
            }
        }

        if (day != -1) {
            plugin.setResetDay(day);
            player.sendMessage(lang.getMessage("message.weekly_reset_day_set", "{day}", itemName));
        }

        adminGUI.openMainMenu(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        adminGUI.removeActiveGui(player.getUniqueId());
    }
}