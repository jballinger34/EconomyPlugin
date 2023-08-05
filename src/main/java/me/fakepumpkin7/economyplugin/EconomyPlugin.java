package me.fakepumpkin7.economyplugin;


import me.fakepumpkin7.economyplugin.commands.*;
import me.fakepumpkin7.economyplugin.listeners.PlayerJoinListener;

import me.fakepumpkin7.economyplugin.listeners.ShopButtonClicked;
import me.fakepumpkin7.economyplugin.listeners.UseMoneyNoteListener;
import me.fakepumpkin7.economyplugin.util.SetupConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class EconomyPlugin extends JavaPlugin {


    private String currency = "£";
    private HashMap<String,Double> playerBank = new HashMap<>();
    private Double startingMoney = 1000.0; //starting cash
    private BaltopCommand baltopExecutor;
    private ShopCommand shopExecutor;
    FileConfiguration balTopConfig;
    SetupConfig setupConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("by FakePumpkin7");

        setupConfig = new SetupConfig(this); //sets starting money and currency

        loadConfigToPlayerBank();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this),this);
        getServer().getPluginManager().registerEvents(new UseMoneyNoteListener(this),this);

        this.getCommand("pay").setExecutor(new PayCommand(this));
        this.getCommand("balance").setExecutor(new BalCommand(this));
        this.getCommand("withdraw").setExecutor(new WithdrawCommand(this));

        shopExecutor = new ShopCommand(this);
        this.getCommand("shop").setExecutor(shopExecutor);
        getServer().getPluginManager().registerEvents(new ShopButtonClicked(this, this.shopExecutor),this);


        baltopExecutor = new BaltopCommand(this);
        baltopExecutor.initBalTop();
        this.getCommand("balancetop").setExecutor(baltopExecutor);

        this.getCommand("sell").setExecutor(new SellCommand(this, shopExecutor.getShopInventories(), this.shopExecutor));
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic

        saveSortedPlayerBankToConfig();
    }





    public void loadConfigToPlayerBank() {
        for(String id: this.getConfig().getKeys(true)) {
            Double balance = this.getConfig().getDouble(id);
            playerBank.put(id, balance);
        }
    }

    public void saveSortedPlayerBankToConfig() {
        /* //cant remember why this code block was here, going to test without it
        for(String key: this.getConfig().getKeys(false)){
            this.getConfig().set(key, null);
        }
        saveConfig();
        */
        for (Map.Entry<String, Double> toStore : baltopExecutor.sortHashMap(playerBank).entrySet()) {
            this.getConfig().set(toStore.getKey(), toStore.getValue());
        }
        saveConfig();

    }


    public Double getBalance(String id) {
        Double bal = playerBank.get(id);
        return bal;
    }
    public boolean checkIfInHashMap(String id){
        if(playerBank.get(id) == null){
            return false;
        }
        else{
            return true;
        }
    }
    public HashMap<String,Double> getPlayerBank(){
        return this.playerBank;
    }


    public Double getStartingMoney() {
        return this.startingMoney;
    }
    public void setStartingMoney(Double amount){
        this.startingMoney = amount;
    }

    public void givePlayerMoney(String id, Double amount){
        Double currentbal = this.playerBank.get(id);
        this.playerBank.replace(id,currentbal+amount);
    }

    public FileConfiguration getBalTopConfig() {
        return balTopConfig;
    }
    public void sortPlayerBank(){
        playerBank = baltopExecutor.sortHashMap(playerBank);
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
