package me.fakepumpkin7.economyplugin.listeners;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class UseMoneyNoteListener implements Listener {


    private EconomyPlugin plugin;

    public UseMoneyNoteListener(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseMoneyNote(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack itemClicked = e.getItem();
        if(itemClicked != null){
            if(e.getHand().equals(EquipmentSlot.HAND)) {
                if (itemClicked.getItemMeta().getDisplayName().equals("" + ChatColor.WHITE + ChatColor.BOLD + "Money Note " + ChatColor.GRAY + "(Right Click)")) {
                    //check lore for amount, pay player for that amount, remove x1 from itemstack
                    Double amount = Double.parseDouble(itemClicked.getItemMeta().getLore().get(0).split(plugin.getCurrency())[1]);

                    //gives player money on the note
                    String id = player.getUniqueId().toString();
                    plugin.givePlayerMoney(id, amount);

                    //removes the note
                    int amountinhand = itemClicked.getAmount();
                    itemClicked.setAmount(amountinhand-1);
                    player.getInventory().setItemInMainHand(itemClicked);
                }
            }
        }
    }
}
