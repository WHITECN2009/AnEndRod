package org.WHITECN.rods;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.anendrod;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.WHITECN.utils.rodsHandler;
import org.WHITECN.utils.useCounter;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class RegularRod implements Listener {
    @EventHandler
    public void onRegularRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals("§2普通末地烛")) {
                event.setCancelled(true);
                if (!player.isSneaking() && player.getCooldown(Material.END_ROD) == 0){
                    mainHand.setItemMeta(useCounter.addTime(meta));
                    meta.setLore(List.of("§7没什么特别的 就是末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                    mainHand.setItemMeta(meta);
                    rodsHandler.handleRegularRod(player);
                }
            }
        }
    }
    @EventHandler
    public void onRegularRod_toEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            ItemStack mainHand = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals("§2普通末地烛") && player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                event.setCancelled(true);
                mainHand.setItemMeta(useCounter.addTime(meta));
                meta.setLore(List.of("§7没什么特别的 就是末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                mainHand.setItemMeta(meta);
                rodsHandler.handleRegularRod(target);
            }
        }
    }
}
