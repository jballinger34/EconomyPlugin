package me.fakepumpkin7.economyplugin.commands;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WithdrawCommand implements CommandExecutor {
    EconomyPlugin plugin;
    DecimalFormat twoForm = new DecimalFormat("##.00");


    public WithdrawCommand(EconomyPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            if(args.length == 1){
                Player player = (Player) sender;
                String id = player.getUniqueId().toString();
                Double balance = plugin.getPlayerBank().get(id);
                Double amount;
                try{
                    amount = Double.parseDouble(args[0]);
                }
                catch (NumberFormatException e){
                    System.out.println(args[0]);
                    return false;
                }
                if(amount <= 0){
                    player.sendMessage(ChatColor.RED + "Cannot withdraw a negative amount");
                    return true;
                }
                Inventory playersInv = player.getInventory();
                int firstEmpty = playersInv.firstEmpty();
                if(firstEmpty == -1){
                    player.sendMessage(ChatColor.RED + "Cannot withdraw with a full inventory");
                    return true;
                }
                if(balance > amount){
                    //withdraw the amount
                    plugin.givePlayerMoney(id, -amount);
                    ItemStack moneynote = createMoneyNote(amount, player);
                    playersInv.setItem(firstEmpty, moneynote);
                    return true;
                }
                player.sendMessage(ChatColor.RED + "Insufficient funds");

            }
        }
        return true;
    }

    private ItemStack createMoneyNote(Double amount, Player player) {
        String playername = player.getName();

        ItemStack moneynote = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = moneynote.getItemMeta();



        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Value " + ChatColor.GRAY + plugin.getCurrency() + twoForm.format(amount));
        lore.add(ChatColor.GOLD + "Signer " + ChatColor.GRAY + playername);


        meta.setDisplayName(""+ChatColor.WHITE+ ChatColor.BOLD + "Money Note "+ ChatColor.GRAY + "(Right Click)");
        meta.setLore(lore);

        moneynote.setItemMeta(meta);
        return moneynote;

    }
}
