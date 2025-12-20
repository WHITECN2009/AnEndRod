package org.WHITECN.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.WHITECN.utils.DeathStatus;
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
import net.md_5.bungee.api.chat.TextComponent;

public class DeathListener implements Listener {

    public static Map<UUID, DeathStatus> mStatus = new HashMap<UUID,DeathStatus>();   // 我不知道怎么描述了喵喵喵
    private Plugin plugin;
    public DeathListener(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID pUuid = player.getUniqueId();
        if (mStatus.containsKey(pUuid)) {
            DeathStatus status = mStatus.remove(pUuid);
            event.setDeathMessage(null);

            Bukkit.getServer().spigot().broadcast(getDeathMessage(status));
        }
    }

    private BaseComponent[] getDeathMessage(DeathStatus status) {
        UUID targetUUID = status.getTarget();
        UUID playerUUID = status.getPlayer();
        ItemStack item  = status.getItemStack();

        String targetName =ChatColor.YELLOW+ Bukkit.getOfflinePlayer(targetUUID).getName();
        String playerName =ChatColor.YELLOW+ Bukkit.getOfflinePlayer(playerUUID).getName();
        boolean suicide   = targetUUID.equals(playerUUID);
        
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
            msg.setText(targetName + "§7 被 " + playerName + "§7 用 " + itemName + " §d插死了喵♥！");
        }

        return new BaseComponent[]{ msg };
    }
}