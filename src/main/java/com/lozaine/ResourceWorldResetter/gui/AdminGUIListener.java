package com.lozaine.ResourceWorldResetter.gui;

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

    public AdminGUIListener(ResourceWorldResetter plugin, AdminGUI adminGUI) {
        this.plugin = plugin;
        this.adminGUI = adminGUI;
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
            case RESET_INTERVAL_MENU:
                handleResetIntervalMenuClick(player, itemName);
                break;
            case RESET_DAY_MENU:
                handleResetDayMenuClick(player, itemName);
                break;
        }
    }

    private void handleMainMenuClick(Player player, String itemName) {
        switch (itemName) {
            case "Change World":
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Enter the world name in chat:");
                break;
            case "Reset Type":
                adminGUI.openResetTypeMenu(player);
                break;
            case "Reset Interval":
                adminGUI.openResetIntervalMenu(player);
                break;
            case "Restart Time":
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Enter the reset hour (0-23) in chat:");
                break;
            case "Warning Time":
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Enter the warning time in minutes:");
                break;
            case "Force Reset":
                player.closeInventory();
                plugin.resetResourceWorld();
                player.sendMessage(ChatColor.GREEN + "World reset initiated!");
                break;
            case "Reload Config":
                player.closeInventory();
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
                break;
        }
    }

    private void handleResetTypeMenuClick(Player player, String itemName) {
        switch (itemName) {
            case "Daily Reset":
                plugin.setResetType("daily");
                player.sendMessage(ChatColor.GREEN + "Reset type set to daily!");
                adminGUI.openMainMenu(player);
                break;
            case "Weekly Reset":
                plugin.setResetType("weekly");
                player.sendMessage(ChatColor.GREEN + "Reset type set to weekly!");
                adminGUI.openResetDayMenu(player);
                break;
            case "Monthly Reset":
                plugin.setResetType("monthly");
                player.sendMessage(ChatColor.GREEN + "Reset type set to monthly!");
                adminGUI.openResetDayMenu(player);
                break;
            case "Back":
                adminGUI.openMainMenu(player);
                break;
        }
    }

    private void handleResetIntervalMenuClick(Player player, String itemName) {
        int interval = switch (itemName) {
            case "1 Hour" -> 3600;
            case "2 Hours" -> 7200;
            case "3 Hours" -> 10800;
            case "4 Hours" -> 14400;
            case "6 Hours" -> 21600;
            case "8 Hours" -> 28800;
            case "12 Hours" -> 43200;
            case "Disable Hourly" -> 86400;
            default -> -1;
        };

        if (interval != -1) {
            plugin.setResetInterval(interval);
            player.sendMessage(ChatColor.GREEN + "Reset interval set to " + (interval / 3600) + " hours!");
        }
        adminGUI.openMainMenu(player);
    }

    private void handleResetDayMenuClick(Player player, String itemName) {
        if (itemName.equals("Back")) {
            adminGUI.openMainMenu(player);
            return;
        }

        if (plugin.getResetType().equalsIgnoreCase("weekly")) {
            int day = switch (itemName) {
                case "Monday" -> 1;
                case "Tuesday" -> 2;
                case "Wednesday" -> 3;
                case "Thursday" -> 4;
                case "Friday" -> 5;
                case "Saturday" -> 6;
                case "Sunday" -> 7;
                default -> -1;
            };
            if (day != -1) {
                plugin.setResetDay(day);
                player.sendMessage(ChatColor.GREEN + "Weekly reset day set to " + itemName + "!");
            }
        } else if (plugin.getResetType().equalsIgnoreCase("monthly")) {
            try {
                int day = Integer.parseInt(itemName.replaceAll("[^0-9]", ""));
                if (day >= 1 && day <= 31) {
                    plugin.setResetDay(day);
                    player.sendMessage(ChatColor.GREEN + "Monthly reset day set to day " + day + "!");
                }
            } catch (NumberFormatException ignored) {}
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
