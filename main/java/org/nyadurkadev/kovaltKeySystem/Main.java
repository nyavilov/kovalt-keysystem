package org.nyadurkadev.kovaltKeySystem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static KeyLogic logic;
    private File dataFile;
    private FileConfiguration dataConfig;

    public void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDataConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        dataFile = new File(getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public static Main getInstance() {
        return instance;
    }

    public static KeyLogic getLogic() {
        return logic;
    }

    @Override
    public void onEnable() {

        instance = this;

        createDataConfig();

        this.logic = new KeyLogic();

        logic.load(dataConfig);

        new KeyUtils().registerRecipes();
        getServer().getPluginManager().registerEvents(new LockLogic(), this);
        getServer().getPluginManager().registerEvents(new ChainLogic(), this);
        // Plugin startup logic
    }

    @Override
    public void onDisable() {

        logic.save(dataConfig);
        saveDataConfig();
        // Plugin shutdown logic
    }
}
