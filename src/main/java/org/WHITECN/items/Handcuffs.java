package org.WHITECN.items;

import java.util.ArrayList;
import java.util.List;

import org.WHITECN.anendrod;
import org.WHITECN.utils.KeyGen;
import org.bukkit.Material;
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

public class Handcuffs implements Listener{
    public static final String name = ChatColor.LIGHT_PURPLE + "手铐♥";
    private static final double RANGE = 1.5;
    public static ItemStack getItem() {
        ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "这是一个手铐，可以限制"
                + ChatColor.YELLOW + "玩家" + ChatColor.LIGHT_PURPLE + "挖掘速度和触碰距离...");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        ItemStack main = e.getMainHandItem();
        ItemStack off  = e.getOffHandItem();
        
        if (main == null || off == null) return;
        if (!main.hasItemMeta() || !off.hasItemMeta()) return;

        /* 名字过滤：主手必须是手铐，副手必须是钥匙 */
        ItemMeta cuffsMeta = main.getItemMeta();
        ItemMeta keyMeta   = off.getItemMeta();
        if (!name.equals(cuffsMeta.getDisplayName()) ||
            !Key.name.equals(keyMeta.getDisplayName())) return;

        List<Integer> cuffsKey = KeyGen.getKey(cuffsMeta);
        List<Integer> keyKey   = KeyGen.getKey(keyMeta);

        /* 规则 1：俩都没数据 → 生成新钥匙并同时写入 */
        if (cuffsKey.isEmpty() && keyKey.isEmpty()) {
            List<Integer> newKey = KeyGen.genKey();          // 生成新钥匙
            cuffsMeta = KeyGen.setKey(cuffsMeta);            // 写入手铐
            keyMeta   = KeyGen.setKey(keyMeta);              // 写入钥匙
            cuffsMeta.addEnchant(org.bukkit.enchantments.Enchantment.BINDING_CURSE, 1, true);
            cuffsMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            applyMeta(main, cuffsMeta);
            applyMeta(off,  keyMeta);
            p.sendMessage(ChatColor.GREEN + "手铐与钥匙已绑定！");
            return;
        }

        /* 规则 2：钥匙有数据，手铐没有 → 把手铐写成钥匙的序列 */
        if (!keyKey.isEmpty() && cuffsKey.isEmpty()) {
            // 把 keyKey 写进手铐
            cuffsMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "the_key"),
                    PersistentDataType.INTEGER_ARRAY,
                    keyKey.stream().mapToInt(Integer::intValue).toArray());
            cuffsMeta.addEnchant(org.bukkit.enchantments.Enchantment.BINDING_CURSE, 1, true);
            cuffsMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            applyMeta(main, cuffsMeta);
            p.sendMessage(ChatColor.GREEN + "手铐已绑定到当前钥匙！");
            return;
        }
        
        p.sendMessage(ChatColor.RED + "这个手铐已经绑定过钥匙了！");
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
        if (!name.equals(meta.getDisplayName())) return;
        if(target.getEquipment().getChestplate() != null) return;

        ItemStack cuffsCopy = mainHand.clone();
        cuffsCopy.setAmount(1);
        target.getInventory().setChestplate(cuffsCopy);
        
        mainHand.setAmount(mainHand.getAmount() - 1);
        user.sendMessage(ChatColor.GREEN + "已给 " + target.getName() + " 戴上手铐！");
        target.sendMessage(ChatColor.RED + "你被戴上了手铐！挖掘速度和触碰距离受限！");
    }
    @EventHandler
    public void onUnlock(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!(e.getRightClicked() instanceof Player)) return;

        Player unlocker = e.getPlayer();
        Player target   = (Player) e.getRightClicked();

        ItemStack key = unlocker.getInventory().getItemInMainHand();
        if (!key.hasItemMeta()) return;
        ItemMeta keyMeta = key.getItemMeta();
        if (!Key.name.equals(keyMeta.getDisplayName())) return;

        ItemStack cuffs = target.getInventory().getChestplate();
        if (cuffs == null || !cuffs.hasItemMeta()) return;
        ItemMeta cuffsMeta = cuffs.getItemMeta();
        if (!name.equals(cuffsMeta.getDisplayName())) return;

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
    private void applyMeta(ItemStack item, ItemMeta meta) {
        item.setItemMeta(meta);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) return;
        if (e.getPlayer().getLocation().distance(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5)) > RANGE) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getLocation().distance(e.getRightClicked().getLocation()) > RANGE) {
            e.setCancelled(true);
        }
    }
}