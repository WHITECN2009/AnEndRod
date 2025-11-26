package org.WHITECN.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class BucketFill {
    public static void fillWhite(Player player, Player target){
        ItemStack offHandItem = player.getEquipment().getItemInOffHand();
        
        if (offHandItem == null || offHandItem.getType() != Material.BUCKET) return;
        if (offHandItem.getAmount() > 1) {
            offHandItem.setAmount(offHandItem.getAmount() - 1);
        } else {
            player.getEquipment().setItemInOffHand(null);
        }
        ItemStack milkBucket = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = milkBucket.getItemMeta();
        meta.setLore(Arrays.asList(
            ChatColor.LIGHT_PURPLE + target.getName() + " 的" + ChatColor.WHITE + "液体!"
        ));
        milkBucket.setItemMeta(meta);
        
        if (player.getInventory().addItem(milkBucket).isEmpty()) {
            // 成功了喵，奇怪的液体!
        } else {
            // 背包满了，掉落物品
            player.getWorld().dropItemNaturally(player.getLocation(), milkBucket);
        }
        
        player.updateInventory();
    }
}
