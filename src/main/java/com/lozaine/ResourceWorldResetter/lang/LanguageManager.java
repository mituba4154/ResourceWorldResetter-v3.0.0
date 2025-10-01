package com.lozaine.ResourceWorldResetter.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class LanguageManager {
    private final JavaPlugin plugin;
    private YamlConfiguration langConfig;
    private String currentLang;

    public LanguageManager(JavaPlugin plugin, String language) {
        this.plugin = plugin;
        this.currentLang = language;
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Save default language files if they don't exist
        saveDefaultLanguageFile("en_us.yml");
        saveDefaultLanguageFile("ja_jp.yml");

        // Load the selected language file
        File langFile = new File(langFolder, currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file " + currentLang + ".yml not found! Falling back to en_us.yml");
            currentLang = "en_us";
            langFile = new File(langFolder, "en_us.yml");
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        plugin.getLogger().log(Level.INFO, "Loaded language: " + currentLang);
    }

    private void saveDefaultLanguageFile(String fileName) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);
        if (!langFile.exists()) {
            try {
                InputStream in = plugin.getResource("lang/" + fileName);
                if (in != null) {
                    Files.copy(in, langFile.toPath());
                    in.close();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save default language file: " + fileName, e);
            }
        }
    }

    public String getMessage(String key) {
        String message = langConfig.getString(key);
        if (message == null) {
            plugin.getLogger().warning("Missing translation key: " + key);
            return key;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    public void reload(String language) {
        this.currentLang = language;
        loadLanguageFile();
    }
}
