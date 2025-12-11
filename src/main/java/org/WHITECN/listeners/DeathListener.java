package org.WHITECN.listeners;

import java.security.Key;
import java.util.Map;
import java.util.UUID;

import org.WHITECN.utils.Status;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DeathListener implements Listener {

    public static Map<UUID, Status> mStatus;   // 我不知道怎么描述了喵喵喵
    private Plugin plugin;
    public DeathListener(Plugin plugin){
        this.plugin = plugin;
    }
    public void onRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID uuid = p.getUniqueId();
                    if (mStatus.containsKey(uuid)) {
                        Status status = mStatus.get(uuid);
                        status.setTime(status.getTime()-1);
                        if (status.getTime() <=0) {
                            mStatus.remove(uuid);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID pUuid = player.getUniqueId();
        if (mStatus.containsKey(pUuid)) {
            Status status = mStatus.remove(pUuid);
            event.setDeathMessage(null);

            Bukkit.getServer().spigot().broadcast(getDeathMessage(status));
        }
    }

    private BaseComponent[] getDeathMessage(Status status) {
        UUID nekoUuid   = status.getNeko();
        UUID playerUuid = status.getPlayer();
        ItemStack item  = status.getItemStack();

        String nekoName   =ChatColor.YELLOW+ Bukkit.getOfflinePlayer(nekoUuid).getName();
        String playerName =ChatColor.YELLOW+ Bukkit.getOfflinePlayer(playerUuid).getName();
        boolean suicide   = nekoUuid.equals(playerUuid);
        
        String itemName;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name().replace('_', ' ').toLowerCase();
        }

        TextComponent msg = new TextComponent();
        if (suicide) {
            msg.setText(playerName + "§7 用 " + itemName + "§7 把自己§d插死了喵♥！");
        } else {
            msg.setText(nekoName + "§7 被 " + playerName + "§7 用 " + itemName + " §d插死了喵♥！");
        }

        return new BaseComponent[]{ msg };
    }
}