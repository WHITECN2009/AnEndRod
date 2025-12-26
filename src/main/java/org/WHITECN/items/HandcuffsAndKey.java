package org.WHITECN.items;

import java.util.ArrayList;
import java.util.List;

import org.WHITECN.anendrod;
import org.WHITECN.utils.KeyGen;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class HandcuffsAndKey implements Listener{
    public static final String handCuffsName = ChatColor.LIGHT_PURPLE + "手铐♥";
    public static final String keyItemName = ChatColor.GRAY + "钥匙";
    private static final double RANGE = 2.5;
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack handCuffs = event.getMainHandItem();   // ← 主手：手铐
        ItemStack keyItem   = event.getOffHandItem();    // ← 副手：钥匙

        if (handCuffs == null || keyItem == null) return;
        if (!handCuffs.hasItemMeta() || !keyItem.hasItemMeta()) return;

        /* 名字过滤：主手必须是手铐，副手必须是钥匙 */
        ItemMeta handCuffsMeta = handCuffs.getItemMeta();
        ItemMeta keyItemMeta   = keyItem.getItemMeta();
        if (!handCuffsName.equals(handCuffsMeta.getDisplayName()) ||
            !keyItemName.equals(keyItemMeta.getDisplayName())) return;

        List<Integer> cuffsCode = KeyGen.getKey(handCuffsMeta);
        List<Integer> keyCode   = KeyGen.getKey(keyItemMeta);

        if (handCuffs.getAmount() > 1 || keyItem.getAmount() > 1) event.getPlayer().sendMessage(ChatColor.RED + "只能进行一次绑定！");

        /* 规则 1：俩都没数据 → 生成新钥匙并同时写入 */
        if (cuffsCode.isEmpty() && keyCode.isEmpty()) {
            List<Integer> newCode = KeyGen.generateKey();
            KeyGen.setKey(handCuffsMeta, newCode);
            KeyGen.setKey(keyItemMeta, newCode);
            handCuffsMeta.addEnchant(org.bukkit.enchantments.Enchantment.BINDING_CURSE, 1, true);
            handCuffsMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            int[] arr = newCode.stream().mapToInt(Integer::intValue).toArray();
            String keyPattern = KeyGen.getKeyShape(newCode);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.LIGHT_PURPLE + "这是一个钥匙，可以解锁也可以上锁");
            lore.add(ChatColor.GRAY + keyPattern);
            keyItemMeta.setLore(lore);

            List<String> lore2 = handCuffsMeta.getLore();
            lore2.add(ChatColor.LIGHT_PURPLE + "需要钥匙: "+ ChatColor.GRAY + keyPattern);
            handCuffsMeta.setLore(lore2);

            handCuffsMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY, arr);
            keyItemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY, arr);

            keyItem.setItemMeta(keyItemMeta);
            handCuffs.setItemMeta(handCuffsMeta);
            player.sendMessage(ChatColor.GREEN + "手铐与钥匙已绑定！");
            return;
        }

        /* 规则 2：钥匙有数据，手铐没有 → 把手铐写成钥匙的序列 */
        if (!keyCode.isEmpty() && cuffsCode.isEmpty()) {
            handCuffsMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY,
                    keyCode.stream().mapToInt(Integer::intValue).toArray());
            handCuffsMeta.addEnchant(org.bukkit.enchantments.Enchantment.BINDING_CURSE, 1, true);
            handCuffsMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            handCuffs.setItemMeta(handCuffsMeta);
            
            player.sendMessage(ChatColor.GREEN + "手铐已绑定到当前钥匙！");
            return;
        }

        player.sendMessage(ChatColor.RED + "这个手铐已经绑定过钥匙了！");
    }
    @EventHandler
    public void onHandCuffs_toEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        Player user  = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        ItemStack mainHand = user.getInventory().getItemInMainHand();
        if (!mainHand.hasItemMeta()) return;
        ItemMeta meta = mainHand.getItemMeta();
        if (!handCuffsName.equals(meta.getDisplayName())) return;
        if(target.getEquipment().getChestplate() != null) return;

        ItemStack cuffsCopy = mainHand.clone();
        cuffsCopy.setAmount(1);
        target.getInventory().setChestplate(cuffsCopy);
        
        mainHand.setAmount(mainHand.getAmount() - 1);
        user.sendMessage(ChatColor.GREEN + "已给 " + target.getName() + " 戴上手铐！");
        target.sendMessage(ChatColor.RED + "你被戴上了手铐！挖掘速度和触碰距离受限！");
    }
    @EventHandler
    public void onUnlock(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        Player unlocker = event.getPlayer();
        Player target   = (Player) event.getRightClicked();

        ItemStack key = unlocker.getInventory().getItemInMainHand();
        if (!key.hasItemMeta()) return;
        ItemMeta keyMeta = key.getItemMeta();
        if (!keyItemName.equals(keyMeta.getDisplayName())) return;

        ItemStack cuffs = target.getInventory().getChestplate();
        if (cuffs == null || !cuffs.hasItemMeta()) return;
        ItemMeta cuffsMeta = cuffs.getItemMeta();
        if (!handCuffsName.equals(cuffsMeta.getDisplayName())) return;

        List<Integer> cuffsCode = KeyGen.getKey(cuffsMeta);
        
        if (cuffsCode.isEmpty()) {
            target.getInventory().setChestplate(null);
            target.getWorld().dropItemNaturally(target.getLocation(), cuffs);
            return;
        }
        
        List<Integer> keyCode = KeyGen.getKey(keyMeta);
        if (keyCode.isEmpty() || !keyCode.equals(cuffsCode)) {
            return;
        }
        
        target.getInventory().setChestplate(null);
        target.getWorld().dropItemNaturally(target.getLocation(), cuffs);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getEquipment().getChestplate() == null) return;
        if (!event.getPlayer().getEquipment().getChestplate().getItemMeta().getDisplayName().equals(handCuffsName)) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) return;
        if (event.getPlayer().getLocation().distance(event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5)) > RANGE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getEquipment().getChestplate() == null) return;
        if (!event.getPlayer().getEquipment().getChestplate().getItemMeta().getDisplayName().equals(handCuffsName)) return;
        if (event.getPlayer().getLocation().distance(event.getRightClicked().getLocation()) > RANGE) {
            event.setCancelled(true);
        }
    }
}