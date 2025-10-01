package com.lozaine.ResourceWorldResetter.commands;

import com.lozaine.ResourceWorldResetter.ResourceWorldResetter;
import com.lozaine.ResourceWorldResetter.lang.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RwrRegionCommand implements CommandExecutor {
    private final ResourceWorldResetter plugin;
    private final LanguageManager lang;

    public RwrRegionCommand(ResourceWorldResetter plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLanguageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("resourceworldresetter.admin")) {
            sender.sendMessage(lang.getMessage("message.no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(lang.getMessage("message.region.usage"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enable":
                plugin.setRegionsEnabled(true);
                sender.sendMessage(lang.getMessage("message.region.enabled"));
                return true;
            case "disable":
                plugin.setRegionsEnabled(false);
                sender.sendMessage(lang.getMessage("message.region.disabled"));
                return true;
            case "list":
                sender.sendMessage(lang.getMessage("message.region.list", 
                        "{regions}", String.join(", ", plugin.getRegionsToReset())));
                return true;
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(lang.getMessage("message.region.add_usage"));
                    return true;
                }
                try {
                    int rx = Integer.parseInt(args[1]);
                    int rz = Integer.parseInt(args[2]);
                    plugin.addRegionToReset(rx, rz);
                    sender.sendMessage(lang.getMessage("message.region.added", 
                            "{x}", String.valueOf(rx), "{z}", String.valueOf(rz)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(lang.getMessage("message.region.invalid_coords"));
                }
                return true;
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(lang.getMessage("message.region.remove_usage"));
                    return true;
                }
                try {
                    int rx = Integer.parseInt(args[1]);
                    int rz = Integer.parseInt(args[2]);
                    plugin.removeRegionToReset(rx, rz);
                    sender.sendMessage(lang.getMessage("message.region.removed", 
                            "{x}", String.valueOf(rx), "{z}", String.valueOf(rz)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(lang.getMessage("message.region.invalid_coords"));
                }
                return true;
            case "addhere":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.getMessage("message.region.player_only"));
                    return true;
                }
                Player player = (Player) sender;
                World world = player.getWorld();
                if (!world.getName().equalsIgnoreCase(plugin.getWorldName())) {
                    sender.sendMessage(lang.getMessage("message.region.wrong_world", 
                            "{world}", plugin.getWorldName()));
                    return true;
                }
                int chunkX = player.getLocation().getBlockX() >> 4;
                int chunkZ = player.getLocation().getBlockZ() >> 4;
                int regionX = chunkX >> 5;
                int regionZ = chunkZ >> 5;
                plugin.addRegionToReset(regionX, regionZ);
                sender.sendMessage(lang.getMessage("message.region.addhere_success", 
                        "{x}", String.valueOf(regionX), "{z}", String.valueOf(regionZ)));
                return true;
            default:
                sender.sendMessage(lang.getMessage("message.region.unknown_subcommand"));
                return true;
        }
    }
}

