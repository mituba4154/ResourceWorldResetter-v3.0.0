package com.lozaine.ResourceWorldResetter.gui;

import com.onarandombox.MultiverseCore.display.ColorAlternator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.lozaine.ResourceWorldResetter.ResourceWorldResetter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminGUI implements Listener {
    private final ResourceWorldResetter plugin;
    private final Map<UUID, GuiType> activeGuis = new HashMap<>();

    public enum GuiType {
        MAIN_MENU,
        RESET_TYPE_MENU,
        RESET_INTERVAL_MENU,
        RESET_DAY_MENU
    }

    public AdminGUI(ResourceWorldResetter plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public GuiType getActiveGuiType(UUID playerId) {
        return activeGuis.get(playerId);
    }

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Resource World Admin");

        gui.setItem(4, createInfoItem(Material.BOOK, "Current Settings",
                "World: " + plugin.getWorldName(),
                "Reset Type: " + plugin.getResetType(),
                "Reset Interval: " + formatInterval(plugin.getResetInterval()),
                "Restart Time: " + plugin.getRestartTime() + ":00",
                "Warning Time: " + plugin.getResetWarningTime() + " minutes"));

        gui.setItem(10, createGuiItem(Material.GRASS_BLOCK, "Change World", "Set which world to reset"));
        gui.setItem(12, createGuiItem(Material.CLOCK, "Reset Type", "Set how often to reset"));
        gui.setItem(14, createGuiItem(Material.HOPPER, "Reset Interval", "Set hourly reset interval"));
        gui.setItem(16, createGuiItem(Material.SUNFLOWER, "Restart Time", "Set daily reset hour"));
        gui.setItem(20, createGuiItem(Material.BELL, "Warning Time", "Set pre-reset warning time"));
        gui.setItem(22, createGuiItem(Material.TNT, "Force Reset", "Reset the world now"));
        gui.setItem(24, createGuiItem(Material.REDSTONE, "Reload Config", "Reload plugin configuration"));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.MAIN_MENU);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!activeGuis.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String itemName = ChatColor.stripColor(meta.getDisplayName());

        switch (itemName) {
            case "Change World" -> {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Use /setworld <worldname> to set the resource world.");
            }
            case "Reset Type" -> openResetTypeMenu(player);
            case "Reset Interval" -> openResetIntervalMenu(player);
            case "Restart Time" -> {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Use /setrestarttime <hour> to set the reset time.");
            }
            case "Warning Time" -> {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Use /setwarningtime <minutes> to set the warning time.");
            }
            case "Force Reset" -> {
                player.closeInventory();
                plugin.resetResourceWorld();
                player.sendMessage(ChatColor.GREEN + "World reset initiated!");
            }
            case "Reload Config" -> {
                player.closeInventory();
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
            }
            case "Back" -> openMainMenu(player);
        }
    }

    public void openResetTypeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Select Reset Type");

        gui.setItem(2, createGuiItem(Material.PAPER, "Daily Reset", "Reset every day"));
        gui.setItem(4, createGuiItem(Material.BOOK, "Weekly Reset", "Reset on a specific day of the week"));
        gui.setItem(6, createGuiItem(Material.CLOCK, "Monthly Reset", "Reset on a specific day of the month"));

        gui.setItem(8, createGuiItem(Material.BARRIER, "Back", "Return to main menu"));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESET_TYPE_MENU);
    }

    public void openResetIntervalMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Select Reset Interval");

        gui.setItem(2, createGuiItem(Material.CLOCK, "1 Hour", "Reset every 1 hour"));
        gui.setItem(3, createGuiItem(Material.CLOCK, "2 Hours", "Reset every 2 hours"));
        gui.setItem(4, createGuiItem(Material.CLOCK, "4 Hours", "Reset every 4 hours"));
        gui.setItem(5, createGuiItem(Material.CLOCK, "6 Hours", "Reset every 6 hours"));
        gui.setItem(6, createGuiItem(Material.BARRIER, "Back", "Return to main menu"));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESET_INTERVAL_MENU);
    }

    public void openResetDayMenu(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Select Reset Day");

        for (int i = 0; i < 7; i++) {
            gui.setItem(i, createGuiItem(Material.PAPER, new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}[i]));
        }
        gui.setItem(8, createGuiItem(Material.BARRIER, "Back", "Return to main menu"));

        player.openInventory(gui);
        activeGuis.put(player.getUniqueId(), GuiType.RESET_DAY_MENU);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
            meta.setLore(Arrays.asList(lore));
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

    private String formatInterval(long seconds) {
        return (seconds >= 86400) ? "Daily" : (seconds <= 0) ? "Disabled" : (seconds / 3600) + " Hours";
    }

    public boolean hasActiveGui(UUID playerUuid) {
        return activeGuis.containsKey(playerUuid);
    }

    public void removeActiveGui(UUID playerUuid) {
        activeGuis.remove(playerUuid);
    }
}
