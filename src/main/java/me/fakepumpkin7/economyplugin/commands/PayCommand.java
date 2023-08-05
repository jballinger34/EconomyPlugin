package me.fakepumpkin7.economyplugin.commands;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;


public class PayCommand implements CommandExecutor {

    public EconomyPlugin plugin;
    DecimalFormat twoForm = new DecimalFormat("##.00");
    public PayCommand(EconomyPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            String senderID = player.getUniqueId().toString();

            Double balance = plugin.getBalance(player.getUniqueId().toString());
            Double amount = Double.valueOf(args[1]);

            if (amount < 0 ){
                player.sendMessage(ChatColor.RED +"You cannot pay a negative amount");
                return true;
            }

            Player receiver = Bukkit.getPlayer(args[0]);
            if(receiver == null){
                player.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }
            String receiverID = receiver.getUniqueId().toString();

            if(amount > balance){
                player.sendMessage(ChatColor.RED +"Insufficient funds");
                return true;
            }
            plugin.givePlayerMoney(senderID, -amount);
            player.sendMessage("You payed " +  ChatColor.YELLOW + ChatColor.BOLD + receiver.getName() +ChatColor.GOLD + ChatColor.BOLD + " " +plugin.getCurrency()+ twoForm.format(amount));

            plugin.givePlayerMoney(receiverID, amount);

            receiver.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + player.getName() + ChatColor.WHITE +" payed you "+ChatColor.GOLD + ChatColor.BOLD +plugin.getCurrency()+ twoForm.format(amount));


        } else {
            Player player2 = Bukkit.getPlayer(args[0]);
            if (player2 != null){
                String id = player2.getUniqueId().toString();
                plugin.givePlayerMoney(id , Double.valueOf(args[1]));
            }
        }
        return true;
    }
}
