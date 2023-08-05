package me.fakepumpkin7.economyplugin.commands;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;


import java.text.DecimalFormat;
import java.util.*;

public class BaltopCommand implements CommandExecutor {
    public EconomyPlugin plugin;
    DecimalFormat twoForm = new DecimalFormat("##.00");

    private HashMap<String,Double> balTop = new HashMap<>();
    private BalTopEntryComparator balTopEntryComparator = new BalTopEntryComparator();


    private long balTopLastUpdated = 0;
    private long balTopCooldown = 60000; //ten min cooldown
    public BaltopCommand(EconomyPlugin plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        long currentTime = System.currentTimeMillis();
        if((currentTime - balTopLastUpdated) > balTopCooldown){
            updateBaltop();
            balTopLastUpdated =  currentTime;
        }
        sender.sendMessage(createBalTopMessage());

        return true;
    }

    private String createBalTopMessage() {
        String message = "" +  ChatColor.GOLD +ChatColor.BOLD + "Top 10 Balances:\n";
        int i = 1;
        for(Map.Entry<String, Double> entry : plugin.getPlayerBank().entrySet()){
            OfflinePlayer player = Bukkit.getOfflinePlayer( UUID.fromString(entry.getKey()) );
            message = message +ChatColor.GOLD + ChatColor.BOLD +  i +") "+ ChatColor.WHITE + player.getName() + ChatColor.GOLD+ ChatColor.BOLD +  " - " + ChatColor.YELLOW + ChatColor.BOLD +  plugin.getCurrency() + twoForm.format(entry.getValue()) + "\n";
            i++;
        }
        return message;
    }


    public void initBalTop(){
        int i = 0;
        for(Map.Entry<String, Double> entry : plugin.getPlayerBank().entrySet()){
            balTop.put(entry.getKey(), entry.getValue());
            i++;
            if(i == 10){
                break;
            }
        }
    }
    public void updateBaltop(){
        plugin.sortPlayerBank();
        int i = 0;
        for(Map.Entry<String, Double> entry : plugin.getPlayerBank().entrySet()){
            balTop.put(entry.getKey(), entry.getValue());
            i++;
            if(i == 10){
                break;
            }
        }

    }

    public HashMap<String,Double> sortHashMap(HashMap<String,Double> hashMap) {
        ArrayList<Map.Entry<String, Double>> list = new ArrayList<>();
        for (Map.Entry<String, Double> entry : hashMap.entrySet()){
            list.add(entry);
        }
        Collections.sort( list , balTopEntryComparator );
        LinkedHashMap<String,Double> map = new LinkedHashMap<>();
        for (int i = 0; i < list.size();i++){
            map.put(list.get(i).getKey(), list.get(i).getValue() );
        }
        return map;
    }
}
 class BalTopEntryComparator implements Comparator< Map.Entry<String,Double> > {

    @Override
    public int compare( Map.Entry<String,Double> entry1,  Map.Entry<String,Double> entry2)
    {
        Double bal1 = entry1.getValue();
        Double bal2 = entry2.getValue();

        if(bal1 > bal2){
            return -1;
        } else if (bal1 < bal2){
            return 1;
        } else{
            return 0;
        }

    }

}
