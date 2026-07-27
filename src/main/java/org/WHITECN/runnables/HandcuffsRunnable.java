package org.WHITECN.runnables;

import org.WHITECN.items.HandcuffsAndKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HandcuffsRunnable extends BukkitRunnable{
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack chestplate = p.getInventory().getChestplate();
            if (chestplate == null || !chestplate.hasItemMeta()) continue;

            ItemMeta meta = chestplate.getItemMeta();
            if (meta == null || !HandcuffsAndKey.handCuffsName.equals(meta.getDisplayName())) continue;

            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20, 9, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 9, false, false));
        }
    }
}
