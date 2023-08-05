package me.fakepumpkin7.economyplugin.listeners;

import me.fakepumpkin7.economyplugin.EconomyPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public EconomyPlugin plugin;

    public PlayerJoinListener(EconomyPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String id = player.getUniqueId().toString();
        if(!plugin.checkIfInHashMap(id)){
            plugin.getPlayerBank().put(id, plugin.getStartingMoney());
        }
    }
}
