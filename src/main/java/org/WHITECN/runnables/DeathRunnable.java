package org.WHITECN.runnables;

import java.util.UUID;

import org.WHITECN.listeners.DeathListener;
import org.WHITECN.utils.DeathStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathRunnable extends BukkitRunnable{
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (DeathListener.mStatus.containsKey(uuid)) {
                DeathStatus status = DeathListener.mStatus.get(uuid);
                status.setTime(status.getTime()-1);
                if (status.getTime() <=0) {
                    DeathListener.mStatus.remove(uuid);
                }
            }
        }
    }
}
