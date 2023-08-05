package me.fakepumpkin7.economyplugin.listeners;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import me.fakepumpkin7.economyplugin.commands.ShopCommand;
import me.fakepumpkin7.economyplugin.util.ShopInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ShopButtonClicked implements Listener {

    private EconomyPlugin plugin;
    private ArrayList<ShopInventory> shopInventories;
    private HashMap<String,List<String>> customBlockNamesAndLore;

    public ShopButtonClicked(EconomyPlugin plugin, ShopCommand shopCommandExecutor) {
        this.plugin = plugin;
        this.shopInventories = shopCommandExecutor.getShopInventories();
        this.customBlockNamesAndLore = shopCommandExecutor.getCustomBlockNamesAndLore();
    }
    @EventHandler
    public void onPlayerClicksInv(InventoryClickEvent e){
        for(ShopInventory si: shopInventories){
            if(si.getTitle().equals(e.getView().getTitle())){
                //the inventory clicked is a shopinventory
                int slot = e.getSlot();
                ItemStack itemClicked = e.getClickedInventory().getItem(slot);

                if (itemClicked != null) {
                    //checks if its a link
                    for (ShopInventory si2 : shopInventories) {
                        if (si2.getTitle().equals(itemClicked.getItemMeta().getDisplayName())) {
                            e.getWhoClicked().openInventory(si2.inventory);
                        }
                    }

                    //checks if its something being sold
                    List<String> lore = itemClicked.getItemMeta().getLore();
                    if (lore != null) {
                        String loreline0 = lore.get(0);
                        if (loreline0.contains("Buy 1 for:")) {
                            String loreline1 = lore.get(1);
                            Double buyprice = Double.parseDouble(loreline0.split(Pattern.quote(plugin.getCurrency()))[1]);
                            Double sellprice = Double.parseDouble(loreline1.split(Pattern.quote(plugin.getCurrency()))[1]);
                            if (e.getClick().equals(ClickType.LEFT)) {
                                buyItem(e.getWhoClicked(), buyprice, itemClicked);
                            }
                            if (e.getClick().equals(ClickType.RIGHT)) {
                                sellItem(e.getWhoClicked(), sellprice, itemClicked);
                            }
                            if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                buyStack(e.getWhoClicked(), buyprice, itemClicked);
                            }
                            if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                sellAll(e.getWhoClicked(), sellprice, itemClicked);
                            }
                        }
                    }
                }


                e.setCancelled(true);
                break;
            }
        }


    }
    private void buyItem(HumanEntity player, Double buyprice, ItemStack itemClicked) {
        String id = player.getUniqueId().toString();
        Double playerBal = plugin.getBalance(id);
        if(playerBal >= buyprice){
            Inventory playerinv = player.getInventory();
            //deals with custom items
            for(Map.Entry<String, List<String>> customnameandvalue : customBlockNamesAndLore.entrySet()){
                String customname = customnameandvalue.getKey();
                if(itemClicked.getItemMeta().getDisplayName().equals(customname)){

                    ItemStack customitemstack = new ItemStack(itemClicked.getType());
                    ItemMeta meta = customitemstack.getItemMeta();
                    meta.setDisplayName(customname);
                    List<String> customlore = customnameandvalue.getValue();
                    meta.setLore(customlore);
                    customitemstack.setItemMeta(meta);

                    for(int i = 0; i< 36; i++){
                        if(playerinv.getItem(i) == null){
                            playerinv.setItem(i,customitemstack);
                            plugin.givePlayerMoney(id, (double) -buyprice);
                            return;
                        }
                        if (playerinv.getItem(i).getType().equals(itemClicked.getType()) && playerinv.getItem(i).getItemMeta().getDisplayName().equals(itemClicked.getItemMeta().getDisplayName())){
                            if(playerinv.getItem(i).getAmount() < 64) {
                                int amounttosetto = playerinv.getItem(i).getAmount() + 1;
                                customitemstack.setAmount(amounttosetto);
                                playerinv.setItem(i, customitemstack);
                                plugin.givePlayerMoney(id, (double) -buyprice);
                                return;
                            }
                        }
                    }
                    player.sendMessage(ChatColor.RED +"Cannot buy with a full inventory");
                    return;
                }
            }
            //deals with regular items
            for(int i = 0; i< 36; i++){
                if(playerinv.getItem(i) == null){
                    playerinv.setItem(i,new ItemStack(itemClicked.getType()));
                    plugin.givePlayerMoney(id, (double) -buyprice);
                    return;
                }
                if (playerinv.getItem(i).getType().equals(itemClicked.getType()) && playerinv.getItem(i).getItemMeta().getDisplayName().equals(itemClicked.getItemMeta().getDisplayName())){
                    if(playerinv.getItem(i).getAmount() < 64) {
                        int amounttosetto = playerinv.getItem(i).getAmount() + 1;
                        playerinv.setItem(i, new ItemStack(itemClicked.getType(), amounttosetto));
                        plugin.givePlayerMoney(id, -buyprice);
                        return;
                    }
                }
            }
            player.sendMessage(ChatColor.RED +"Cannot buy with a full inventory");
            return;
        }
        player.sendMessage(ChatColor.RED +"Insufficient Funds");
    }
    private void buyStack(HumanEntity player, Double buyprice, ItemStack itemClicked) {
        String id = player.getUniqueId().toString();
        Double playerBal = plugin.getBalance(id);
        if(playerBal >= 64*buyprice){
            Inventory playerinv = player.getInventory();

            for(Map.Entry<String, List<String>> customnameandvalue : customBlockNamesAndLore.entrySet()){
                String customname = customnameandvalue.getKey();
                if(itemClicked.getItemMeta().getDisplayName().equals(customname)){
                    ItemStack customitemstack = new ItemStack(itemClicked.getType());
                    customitemstack.setAmount(64);
                    ItemMeta meta = customitemstack.getItemMeta();
                    meta.setDisplayName(customname);
                    List<String> customlore = customnameandvalue.getValue();
                    meta.setLore(customlore);
                    customitemstack.setItemMeta(meta);

                    for(int i = 0; i< 36; i++){
                        if(playerinv.getItem(i) == null){
                            playerinv.setItem(i,customitemstack);
                            plugin.givePlayerMoney(id, -64.0*buyprice);
                            return;
                        }
                    }
                    player.sendMessage(ChatColor.RED +"Cannot buy with a full inventory");
                    return;
                }
            }
            //deals with buying stack of normal item
            for(int i = 0; i < 36; i++){
                if(playerinv.getItem(i) == null){
                    playerinv.setItem(i,new ItemStack(itemClicked.getType(),64));
                    plugin.givePlayerMoney(id, -(64.0*buyprice));
                    return;
                }
            }
            player.sendMessage(ChatColor.RED +"Cannot buy with a full inventory");
            return;

        }
        player.sendMessage(ChatColor.RED +"Insufficient Funds");
    }


    private void sellItem(HumanEntity player, Double sellprice, ItemStack itemClicked) {
        String id = player.getUniqueId().toString();
        Inventory playerinv = player.getInventory();
        for(int i = 0; i< 36; i++){
            if(playerinv.getItem(i) != null) {
                if (playerinv.getItem(i).getType().equals(itemClicked.getType()) && itemClicked.getItemMeta().getDisplayName().equals(playerinv.getItem(i).getItemMeta().getDisplayName())) {
                    //here names and item type are the same
                    if(customBlockNamesAndLore.get(itemClicked.getItemMeta().getDisplayName()) != null){
                        String customname = itemClicked.getItemMeta().getDisplayName();
                        if(customBlockNamesAndLore.get(customname).equals(playerinv.getItem(i).getLore())) {
                            //the item is a custom item
                            ItemStack customitemstack = new ItemStack(itemClicked.getType());
                            List<String> customlore = customBlockNamesAndLore.get(customname);
                            ItemMeta meta = customitemstack.getItemMeta();
                            int amount = playerinv.getItem(i).getAmount() - 1;

                            meta.setDisplayName(customname);
                            meta.setLore(customlore);

                            customitemstack.setItemMeta(meta);
                            customitemstack.setAmount(amount);

                            playerinv.setItem(i, customitemstack);
                            plugin.givePlayerMoney(id, (double) sellprice);
                            return;
                        }
                    }
                    if(customBlockNamesAndLore.get(itemClicked.getItemMeta().getDisplayName()) == null) {
                        int amount = playerinv.getItem(i).getAmount();
                        playerinv.setItem(i, new ItemStack(playerinv.getItem(i).getType(), amount - 1));
                        plugin.givePlayerMoney(id, (double) sellprice);
                        return;
                    }
                }
            }
        }
        player.sendMessage(ChatColor.RED + "You do not have any of this item");
    }
    private void sellAll(HumanEntity player, Double sellprice, ItemStack itemClicked) {
        int amount = 0;
        String id = player.getUniqueId().toString();
        Inventory playerinv = player.getInventory();
        for(int i = 0; i< 36; i++){
            if(playerinv.getItem(i) != null) {
                if (playerinv.getItem(i).getType().equals(itemClicked.getType())) {
                    if(customBlockNamesAndLore.get(itemClicked.getItemMeta().getDisplayName()) != null){
                        String customname = itemClicked.getItemMeta().getDisplayName();
                        if(customBlockNamesAndLore.get(customname).equals(playerinv.getItem(i).getLore())){
                            amount += playerinv.getItem(i).getAmount();
                            playerinv.setItem(i, null);
                        }
                    }


                    if (customBlockNamesAndLore.get(itemClicked.getItemMeta().getDisplayName()) == null){
                        amount += playerinv.getItem(i).getAmount();
                        playerinv.setItem(i, null);
                    }
                }
            }
        }

        plugin.givePlayerMoney(id, (double) amount * sellprice);

    }

}
