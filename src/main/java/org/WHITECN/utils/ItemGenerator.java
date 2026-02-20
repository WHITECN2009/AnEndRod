package org.WHITECN.utils;

import net.md_5.bungee.api.ChatColor;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemGenerator {
    public static ItemStack createHandCuffs(){
        ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d手铐♥");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "这是一个手铐，可以限制"
                + ChatColor.YELLOW + "玩家" + ChatColor.LIGHT_PURPLE + "挖掘速度和触碰距离...");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(),"code"),
                PersistentDataType.INTEGER_ARRAY, new int[] {0}
        );
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createKeyItem() {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7钥匙");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "这是一个钥匙，可以解锁也可以上锁");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(),"code"),
                PersistentDataType.INTEGER_ARRAY, new int[] {0}
        );
        item.setItemMeta(meta);
        return item;

    }
}
