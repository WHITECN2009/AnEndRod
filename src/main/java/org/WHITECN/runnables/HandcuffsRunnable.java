package org.WHITECN.runnables;

import org.WHITECN.items.HandcuffsAndKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HandcuffsRunnable extends BukkitRunnable{
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getEquipment().getChestplate() != null) {
                if (p.getEquipment().getChestplate().getItemMeta().getDisplayName().equals(HandcuffsAndKey.handCuffsName)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20*1, 9, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*1, 9, false, false));
                }
            }
        }
    }
}
