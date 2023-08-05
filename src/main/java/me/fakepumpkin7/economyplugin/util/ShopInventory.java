package me.fakepumpkin7.economyplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ShopInventory {
    public Inventory inventory;
    private String title;

    public ShopInventory(int size, String title) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public String getTitle() {
        return title;
    }

}
