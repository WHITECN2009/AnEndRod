package org.WHITECN.rods;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.WHITECN.utils.BucketFill;
import org.WHITECN.utils.rodsHandler;
import org.WHITECN.utils.useCounter;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SlimeRod implements Listener {
    @EventHandler
    public void onSlimeRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(Vars.SLIME_ROD_NAME)) {
                event.setCancelled(true);
                mainHand.setItemMeta(useCounter.addTime(meta));
                meta.setLore(List.of("§7一个黏糊糊的末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                mainHand.setItemMeta(meta);
                rodsHandler.handleSlimeRod(player);
            }
        }
    }
    @EventHandler
    public void onSlimeRod_toEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player){
            Player target = (Player) event.getRightClicked();
            ItemStack mainHand = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(Vars.SLIME_ROD_NAME)) {
                event.setCancelled(true);
                if (target.getEquipment().getLeggings() != null) {
                    player.sendMessage(ChatColor.RED + target.getDisplayName() + "还穿着呢！");
                    return;
                }
                mainHand.setItemMeta(useCounter.addTime(meta));
                meta.setLore(List.of("§7一个黏糊糊的末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                mainHand.setItemMeta(meta);
                rodsHandler.handleSlimeRod(target);
                if (new Random().nextInt(100) < Vars.FILL_CHANCE*100) {
                    BucketFill.fillWhite(player, target);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "你得到了" + target.getName() + " 的" + ChatColor.WHITE + "液体!");
                }
            }
        }
    }
}
