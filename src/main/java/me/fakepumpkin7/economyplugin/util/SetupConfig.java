package me.fakepumpkin7.economyplugin.util;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class SetupConfig {
    private EconomyPlugin plugin;
    private FileConfiguration setupConfig;
    private File setupConfigFile;

    public SetupConfig(EconomyPlugin plugin) {
        this.plugin = plugin;
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        setupConfigFile = new File(plugin.getDataFolder(), "setupconfig.yml");
        setupConfig = YamlConfiguration.loadConfiguration(setupConfigFile);

        if (setupConfig.getKeys(true).size() == 0){
            loadFromDefaults();
        }

        Double amount = setupConfig.getDouble("startingmoney");
        String currency = setupConfig.getString("currency");
        plugin.setStartingMoney(amount);
        plugin.setCurrency(currency);
    }

    public FileConfiguration getSetupConfig(){
        if (setupConfig == null) {
            loadFromDefaults();
        }
        return setupConfig;
    }

    private void loadFromDefaults() {
        InputStream defaults = plugin.getResource("setupconfigdefaults.yml");
        try (FileOutputStream outputStream = new FileOutputStream(setupConfigFile, false)) {
            int read;
            byte[] bytes = new byte[8192]; //default buffer size
            while ((read = defaults.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            setupConfig = YamlConfiguration.loadConfiguration(setupConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveSetupConfig();
    }

    private void saveSetupConfig() {
        if (setupConfig == null || setupConfigFile == null) {
            return;
        }
        try {
            getSetupConfig().save(setupConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + setupConfigFile, ex);
        }
    }



}
