package org.WHITECN.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.WHITECN.anendrod;
import org.WHITECN.utils.KeyGen;
import org.WHITECN.utils.tagUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

import static org.WHITECN.anendrod.prefix;

public class HandcuffsAndKey implements Listener{
    public static final String handCuffsName = ChatColor.LIGHT_PURPLE + "手铐♥";
    public static final String keyItemName = ChatColor.GRAY + "钥匙";
    private static final double RANGE = 2.5;
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack main = event.getOffHandItem(); //实际上是获取切换 到 副手的物品 即切换前主手的物品
        ItemStack off = event.getMainHandItem(); //我不知道为什么要这样设置这个API 这玩意好他妈怪
        
            if (main == null || off == null) return;
        if (!main.hasItemMeta() || !off.hasItemMeta()) return;

        /* 名字过滤：主手必须是手铐，副手必须是钥匙 */
        ItemMeta mainMeta = main.getItemMeta();
        ItemMeta offMeta = off.getItemMeta();
        if (!(handCuffsName.equals(mainMeta.getDisplayName())) || !(keyItemName.equals(offMeta.getDisplayName()))) return;

        List<Integer> cuffsCode = KeyGen.getKey(mainMeta);
        List<Integer> keyCode   = KeyGen.getKey(offMeta);

        if (main.getAmount() > 1 || off.getAmount() > 1) event.getPlayer().sendMessage(ChatColor.RED + "只能进行一次绑定！");

        /* 规则 1：俩都没数据 → 生成新钥匙并同时写入 */
        if (cuffsCode.isEmpty() && keyCode.isEmpty()) {
            List<Integer> newCode = KeyGen.generateKey();
            KeyGen.setKey(mainMeta, newCode);
            KeyGen.setKey(offMeta, newCode);
            mainMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            mainMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            int[] arr = newCode.stream().mapToInt(Integer::intValue).toArray();
            String keyPattern = KeyGen.getKeyShape(newCode);
            List<String> offLore = new ArrayList<>();
            offLore.add(ChatColor.LIGHT_PURPLE + "这是一个钥匙，可以解锁也可以上锁");
            offLore.add(ChatColor.GRAY + keyPattern);
            offMeta.setLore(offLore);

            List<String> mainLore = mainMeta.getLore();
            mainLore.add(ChatColor.LIGHT_PURPLE + "需要钥匙: "+ ChatColor.GRAY + keyPattern);
            mainMeta.setLore(mainLore);

            mainMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY, arr);
            offMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY, arr);

            main.setItemMeta(mainMeta);
            off.setItemMeta(offMeta);
            player.sendMessage(ChatColor.GREEN + "手铐与钥匙已绑定！");
            return;
        }

        /* 规则 2：钥匙有数据，手铐没有 → 把手铐写成钥匙的序列 */
        if (!keyCode.isEmpty() && cuffsCode.isEmpty()) {
            // 把 keyCode 写进手铐
            mainMeta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), "code"),
                    PersistentDataType.INTEGER_ARRAY,
                    keyCode.stream().mapToInt(Integer::intValue).toArray());
            mainMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            mainMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            main.setItemMeta(mainMeta);
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
        tagUtils.ensureTag(target,"canCuff","false");
        ItemStack mainHand = user.getInventory().getItemInMainHand();
        if (!mainHand.hasItemMeta()) return;
        if (tagUtils.getTag(target,"canCuff").equals("false")){
            user.sendMessage(prefix + "§c该玩家已禁用手铐玩法！");
            return;
        }
        ItemMeta meta = mainHand.getItemMeta();
        if (!handCuffsName.equals(meta.getDisplayName())) return;

        // 检查目标是否已经佩戴了手铐
        ItemStack currentChestplate = target.getEquipment().getChestplate();
        if (currentChestplate != null && currentChestplate.hasItemMeta() && handCuffsName.equals(currentChestplate.getItemMeta().getDisplayName())) {
            user.sendMessage(prefix + "§c该玩家已经戴着手铐了！");
            return;
        }

        // 优先将原有胸甲放入物品栏，如果满了则掉落在地上
        if (currentChestplate != null && currentChestplate.getType() != Material.AIR) {
            if (!target.getInventory().addItem(currentChestplate).isEmpty()) {
                target.getWorld().dropItemNaturally(target.getLocation(), currentChestplate);
            }
        }

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

    @EventHandler
    public void checkSelfCuff(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null || event.getPlayer().getInventory().getItemInOffHand().getItemMeta() != null) {
            if (event.getItem().getItemMeta().getDisplayName().equals("§d手铐♥")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(prefix + "§c想要自缚嘛？不可以哦");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 阻止直接点击盔甲槽放入手铐
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getRawSlot() == 6) { // 6 是胸甲槽位
            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.hasItemMeta() && handCuffsName.equals(cursor.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(prefix + "§c想要自缚嘛？不可以哦");
                return;
            }
        }
        // 处理 Shift 点击
        if (event.isShiftClick() && event.getCurrentItem() != null) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked.hasItemMeta() && handCuffsName.equals(clicked.getItemMeta().getDisplayName())) {
                // 如果是玩家自己的背包界面
                if (event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() == InventoryType.PLAYER) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(prefix + "§c想要自缚嘛？不可以哦");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.LEFT_CLICK_AIR) || !(event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (event.getPlayer().getEquipment().getChestplate() != null) {
            if (event.getPlayer().getEquipment().getChestplate().getItemMeta().getDisplayName().equals("§d手铐♥")) {
                if (event.getPlayer().getLocation().distance(Objects.requireNonNull(event.getClickedBlock()).getLocation().add(0.5, 0.5, 0.5)) > RANGE) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getEquipment().getChestplate() != null) {
            if (event.getPlayer().getEquipment().getChestplate().getItemMeta().getDisplayName().equals("§d手铐♥")) {
                if (event.getPlayer().getLocation().distance(event.getRightClicked().getLocation()) > RANGE) {
                    event.setCancelled(true);
                }
            }
        }
    }
}