package me.fakepumpkin7.economyplugin.commands;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import me.fakepumpkin7.economyplugin.util.ShopInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;


import java.io.*;
import java.util.*;
import java.util.logging.Level;


public class ShopCommand implements CommandExecutor {
    private EconomyPlugin plugin;
    private FileConfiguration shopConfig;
    private File shopConfigFile;
    private ArrayList<ShopInventory> shopInventories;
    private HashMap<String,List<String>> customBlockNamesAndLore;
    public ShopCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        shopConfigFile = new File(plugin.getDataFolder(), "shopconfig.yml");
        shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile);

        if (shopConfig.getKeys(true).size() == 0){
            loadFromDefaults();
        }
        this.shopInventories = new ArrayList<>();
        this.customBlockNamesAndLore = new HashMap<>();
        parseYMLConfigToInventories();

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            player.openInventory(shopInventories.get(0).inventory);
        }
        return true;
    }




    private void loadFromDefaults() {
        InputStream defaults = plugin.getResource("shopconfigdefaults.yml");
        try (FileOutputStream outputStream = new FileOutputStream(shopConfigFile, false)) {
            int read;
            byte[] bytes = new byte[8192]; //default buffer size
            while ((read = defaults.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveShopConfig();
    }

    public FileConfiguration getShopConfig(){
        if (shopConfig == null) {
            loadFromDefaults();
        }
        return shopConfig;
    }
    public void saveShopConfig() {
        if (shopConfig == null || shopConfigFile == null) {
            return;
        }
        try {
            getShopConfig().save(shopConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + shopConfigFile, ex);
        }
    }




    private void parseYMLConfigToInventories() {
        for(String pageName: shopConfig.getKeys(false)){
            ConfigurationSection pageInfo = shopConfig.getConfigurationSection(pageName);

            String inventoryTitle = pageInfo.getString("inventoryTitle");


            ConfigurationSection inventoryDimensions = pageInfo.getConfigurationSection( "inventoryDimensions");
            int inventoryRows = inventoryDimensions.getInt("rows");
            int inventoryColumns = inventoryDimensions.getInt("columns");

            ShopInventory shopInventory = new ShopInventory(inventoryRows*inventoryColumns,inventoryTitle);
            Inventory inventory = shopInventory.inventory;

            ConfigurationSection links = pageInfo.getConfigurationSection("links");
            if(links != null) {
                for (String link : links.getKeys(false)) {
                    ConfigurationSection linkInfo = links.getConfigurationSection(link);

                    String title = linkInfo.getString("opens");
                    String block = linkInfo.getString("block");
                    List<String> lore = linkInfo.getStringList("lore");
                    int slot = linkInfo.getInt("slot");

                    ItemStack item = new ItemStack(Material.getMaterial(block.toUpperCase()), 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(title);
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    inventory.setItem(slot, item);
                }
            }
            ConfigurationSection selling = pageInfo.getConfigurationSection("selling");
            if(selling != null) {
                for (String sell : selling.getKeys(false)) {
                    ConfigurationSection sellingInfo = selling.getConfigurationSection(sell);
                    String block = sellingInfo.getString("block");
                    String customblockname = sellingInfo.getString("customblockname");
                    List<String> customblocklore = sellingInfo.getStringList("customblocklore");
                    customBlockNamesAndLore.put(customblockname,customblocklore);
                    int slot = sellingInfo.getInt("slot");
                    Double buyPrice = sellingInfo.getDouble("buyprice");
                    Double sellPrice = sellingInfo.getDouble("sellprice");

                    ItemStack item = new ItemStack(Material.getMaterial(block), 1);
                    ItemMeta meta = item.getItemMeta();
                    List<String> itemlores = new ArrayList<>();
                    itemlores.add(ChatColor.GRAY + "Buy 1 for: "+ChatColor.YELLOW+ ChatColor.BOLD +plugin.getCurrency() + buyPrice);
                    itemlores.add(ChatColor.GRAY +"Sell 1 for: " +ChatColor.YELLOW+ ChatColor.BOLD +plugin.getCurrency() +sellPrice);
                    meta.setLore(itemlores);
                    meta.setDisplayName(customblockname);
                    item.setItemMeta(meta);

                    inventory.setItem(slot, item);
                }
            }
            ConfigurationSection tileBackGround = pageInfo.getConfigurationSection("tileBackGround");
            boolean BGenabled = tileBackGround.getBoolean("enabled");
            String BGcolour = tileBackGround.getString("colour");
            if(BGenabled){
                ItemStack BGcolouredStainedGlass = new ItemStack(Material.getMaterial(BGcolour.toUpperCase() +"_STAINED_GLASS_PANE"),1);
                ItemMeta meta = BGcolouredStainedGlass.getItemMeta();
                meta.setDisplayName(" ");
                BGcolouredStainedGlass.setItemMeta(meta);

                for(int i = 0; i< inventory.getSize(); i++){
                    if(inventory.getItem(i) == null){
                        inventory.setItem(i, BGcolouredStainedGlass);
                    }
                }
            }


            ConfigurationSection tileBackGroundAccent = pageInfo.getConfigurationSection("tileBackGroundAccent");
            boolean ACCENTenabled = tileBackGroundAccent.getBoolean("enabled");
            String ACCENTcolour = tileBackGroundAccent.getString("colour");
            String ACCENTpattern = tileBackGroundAccent.getString("pattern");
            if(ACCENTenabled){
                ItemStack ACCENTcolouredStainedGlass = new ItemStack(Material.getMaterial(ACCENTcolour.toUpperCase() +"_STAINED_GLASS_PANE"),1);
                ItemMeta meta2 = ACCENTcolouredStainedGlass.getItemMeta();
                meta2.setDisplayName(" ");
                ACCENTcolouredStainedGlass.setItemMeta(meta2);
                fillInvWithPattern(inventory,ACCENTcolouredStainedGlass, ACCENTpattern, inventoryRows, inventoryColumns);
                }


            shopInventories.add(shopInventory);
        }
    }

    private void fillInvWithPattern(Inventory inv, ItemStack accent, String pattern,int inventoryRows, int inventoryColumns) {
        if(pattern.equals("ring")){
            for(int i = 0; i< inv.getSize(); i++){
                if(inv.getItem(i) == null){
                    if(i % inventoryColumns==0 || i/inventoryColumns==0 || i % inventoryColumns == (inventoryColumns - 1)  || i/inventoryColumns == (inventoryRows - 1)){ //if(onfirstcol,ontoprow,onlastcol, onlastrow
                        inv.setItem(i,accent);
                    }
                }
            }
        }
        if(pattern.equals("checkered")){
            for(int i = 0; i< inv.getSize(); i++){
                if(inv.getItem(i) == null){
                    if(i%2 == 1){
                        inv.setItem(i,accent);
                    }
                }
            }
        }
        if(pattern.equals("topline")){
            for(int i = 0; i< inv.getSize(); i++){
                if(inv.getItem(i) == null){
                    if(i/inventoryColumns==0){
                        inv.setItem(i,accent);
                    }
                }
            }
        }


    }


    public ArrayList<ShopInventory> getShopInventories() {
        return shopInventories;
    }
    public HashMap<String, List<String>> getCustomBlockNamesAndLore(){
        return customBlockNamesAndLore;
    }
}


