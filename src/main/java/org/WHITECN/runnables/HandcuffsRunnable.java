package org.WHITECN.runnables;

import org.WHITECN.items.HandcuffsAndKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class HandcuffsRunnable extends BukkitRunnable{
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Objects.requireNonNull(Objects.requireNonNull(p.getEquipment()).getChestplate()).getItemMeta() != null) {
                if (p.getEquipment().getChestplate().getItemMeta().getDisplayName().equals(HandcuffsAndKey.handCuffsName)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20, 9, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 9, false, false));
                }
            }
        }
    }
}
