package me.fakepumpkin7.economyplugin.commands;




import me.fakepumpkin7.economyplugin.EconomyPlugin;
import me.fakepumpkin7.economyplugin.util.IntDouble;
import me.fakepumpkin7.economyplugin.util.ShopInventory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class SellCommand implements CommandExecutor {

    private ArrayList<ShopInventory> shopInventories;
    private EconomyPlugin plugin;
    private HashMap<String,List<String>> customBlockNamesAndLore;

    public SellCommand(EconomyPlugin plugin, ArrayList<ShopInventory> si, ShopCommand shopCommandExecutor) {
        this.plugin = plugin;
        this.shopInventories = si;
        this.customBlockNamesAndLore = shopCommandExecutor.getCustomBlockNamesAndLore();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                if (args[0].equals("hand")) {
                    sellHand(player);
                    return true;
                }
                if (args[0].equals("all")) {
                    sellAll(player);
                    return true;
                }

            }
        }
        return false;
    }

   private void sellHand(Player player){
        ItemStack tosell = player.getInventory().getItemInMainHand();
        if(tosell != null){
            if(tosell.hasItemMeta()){
                IntDouble res = sellHandCustom(player, tosell);
                player.sendMessage("Sold "+ res.first + " items for " +plugin.getCurrency()+res.second);
            } else {
                IntDouble res = sellHandDefault(player, tosell);
                player.sendMessage("Sold "+ res.first + " items for " +plugin.getCurrency()+res.second);
            }
        }
        //get item in main hand, all of this item in inv will be sold

        //check if this item is custom or not, so basically if it has itemmeta
   }

   //pass sellHandCustom an itemstack that has itemmeta
   private IntDouble sellHandCustom(Player player, ItemStack tosell){
       String id = player.getUniqueId().toString();
       int amountsold = 0;
       Double addedtobal = 0.0;
       Material toselltype = tosell.getType();
       String tosellname = tosell.getItemMeta().getDisplayName();
       List<String> toselllore = tosell.getItemMeta().getLore();
       for(ShopInventory shopinv : shopInventories){
           for(ItemStack shopinvitem : shopinv.inventory){
               //check if types are the same
               if(shopinvitem.getType().equals(toselltype) && shopinvitem.hasItemMeta() && shopinvitem.getItemMeta().getDisplayName().equals(tosellname)  && shopinvitem.getItemMeta().hasLore() && shopinvitem.getItemMeta().getLore().size() == 2 && shopinvitem.getItemMeta().getLore().get(1).contains("Sell 1 for")) {
                   if (customBlockNamesAndLore.get(shopinvitem.getItemMeta().getDisplayName())!= null) {
                       if (customBlockNamesAndLore.get(shopinvitem.getItemMeta().getDisplayName()).equals(toselllore)) {
                           //item is sellable
                           Double sellprice = Double.parseDouble(shopinvitem.getItemMeta().getLore().get(1).split(Pattern.quote(plugin.getCurrency()))[1]);

                           for (ItemStack item : player.getInventory().getStorageContents()) {
                               if (item != null && item.getType().equals(toselltype) && item.getItemMeta().getDisplayName().equals(tosellname) && item.getItemMeta().getLore().equals(toselllore)) {
                                   //sells every item in inv that is same as item
                                   int amount = item.getAmount();


                                   Double amounttogiveplayer = amount * sellprice;

                                   amountsold += amount;
                                   addedtobal += amounttogiveplayer;

                                   item.setAmount(0);
                                   plugin.givePlayerMoney(id, amounttogiveplayer);


                               }
                           }
                           return new IntDouble(amountsold, addedtobal);
                       }
                   }
               }
           }
       }
       return new IntDouble(amountsold,addedtobal);
   }


   //pass sellHandDefault am itemstack with no itemmeta
   private IntDouble sellHandDefault(Player player, ItemStack tosell){
        String id = player.getUniqueId().toString();
        int amountsold = 0;
        Double addedtobal = 0.0;
        Material toselltype = tosell.getType();
        //loop through shopinventory slots
        for(ShopInventory shopinv : shopInventories){
            for(ItemStack shopinvitem : shopinv.inventory){
                //check if types are the same
                if(shopinvitem != null && shopinvitem.getType().equals(toselltype) && shopinvitem.hasItemMeta() && !shopinvitem.getItemMeta().hasDisplayName() && shopinvitem.getItemMeta().hasLore() && shopinvitem.getItemMeta().getLore().size() == 2 && shopinvitem.getItemMeta().getLore().get(1).contains("Sell 1 for")){
                    //tosell is sellable
                    Double sellprice = Double.parseDouble(shopinvitem.getItemMeta().getLore().get(1).split(Pattern.quote(plugin.getCurrency()))[1]);


                    for(ItemStack item : player.getInventory().getStorageContents()){
                        if(item != null && item.getType().equals(toselltype)){
                            //sells every item in inv that is same as item
                            int amount = item.getAmount();


                            Double amounttogiveplayer = amount*sellprice;


                            amountsold += amount;
                            addedtobal += amounttogiveplayer;

                            item.setAmount(0);
                            plugin.givePlayerMoney(id,amounttogiveplayer);

                        }
                    }
                    return new IntDouble(amountsold,addedtobal);
                }

            }
        }
       return new IntDouble(amountsold,addedtobal);
   }



   private void sellAll(Player player){
        int amountsold = 0;
        Double addedtobal = 0.0;
        for(ItemStack item : player.getInventory().getStorageContents()){
            if (item != null){
                if(item.hasItemMeta()){
                    IntDouble res = sellHandCustom(player, item);
                    amountsold += res.first;
                    addedtobal += res.second;

                } else {
                    IntDouble res = sellHandDefault(player, item);
                    amountsold += res.first;
                    addedtobal += res.second;
                }
            }
        }
        player.sendMessage("Sold "+ amountsold + " items for " +plugin.getCurrency()+addedtobal);
   }



}


