package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.anendrod;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.WHITECN.utils.DeathStatus;
import org.WHITECN.utils.rodsHandler;
import org.WHITECN.utils.useCounter;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class SlimeRod implements Listener {
    @EventHandler
    public void onSlimeRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals("§a粘液§2末地烛")) {
                event.setCancelled(true);
                if (!player.isSneaking() && player.getCooldown(Material.END_ROD) == 0){
                    mainHand.setItemMeta(useCounter.addTime(meta));
                    meta.setLore(List.of("§7一个黏糊糊的末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                    mainHand.setItemMeta(meta);
                    DeathStatus.add(player.getUniqueId(), player.getUniqueId(), 10*20, mainHand); //10second,这10second里面玩家死了就是被我插的呢！
                    rodsHandler.handleSlimeRod(player,player);
                }
            }
        }
    }
    @EventHandler
    public void onSlimeRod_toEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player){
            Player target = (Player) event.getRightClicked();
            ItemStack mainHand = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals("§a粘液§2末地烛")) {
                // 拦截粘液末地烛对玩家的交互
                event.setCancelled(true);
                
                if (player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                    if (target.getEquipment().getLeggings() != null){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c怎么穿着裤子喵!"));
                        return;
                    }
                    mainHand.setItemMeta(useCounter.addTime(meta));
                    meta.setLore(List.of("§7一个黏糊糊的末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                    mainHand.setItemMeta(meta);
                    DeathStatus.add(player.getUniqueId(), target.getUniqueId(), 10*20, mainHand); //10second,这10second里面玩家死了就是被我插的呢！
                    rodsHandler.handleSlimeRod(event.getPlayer(),target);
                }
            }
        }
    }
}
