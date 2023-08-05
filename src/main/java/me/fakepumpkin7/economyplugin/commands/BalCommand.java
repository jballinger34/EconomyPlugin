package me.fakepumpkin7.economyplugin.commands;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import net.kyori.adventure.chat.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class BalCommand implements CommandExecutor {

    public EconomyPlugin plugin;
    DecimalFormat twoForm = new DecimalFormat("##.00");

    public BalCommand(EconomyPlugin plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                String id = player.getUniqueId().toString();
                Double balance = plugin.getBalance(id);
                player.sendMessage(ChatColor.WHITE + "Your balance is: " +  ChatColor.GOLD  + ChatColor.BOLD + plugin.getCurrency() + twoForm.format(balance));
                return true;
            } else {
                Player otherPlayer = Bukkit.getPlayer(args[0]);
                if (otherPlayer != null){
                    String otherid = otherPlayer.getUniqueId().toString();
                    Double otherbalance = plugin.getBalance(otherid);

                    player.sendMessage(""+ChatColor.YELLOW + ChatColor.BOLD + otherPlayer.getName()+"'s " +ChatColor.WHITE+"balance is: " +  ChatColor.GOLD + ChatColor.BOLD +  plugin.getCurrency() +  twoForm.format(otherbalance));
                    return true;
                }
            }
        } else {
            plugin.getLogger().info("Console cannot use this command");
        }

        return true;
    }
}
