package org.WHITECN.rods;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.useCounter;
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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractRod implements Listener {
    protected String displayName;
    protected String namespaceName;
    protected Integer cooldown;
    protected List<String> baseLore;
    protected ShapelessRecipe recipe;
    protected Map<NamespacedKey, Object> persistentData;
    protected Map<NamespacedKey, PersistentDataType<?, ?>> persistentDataTypes;
    
    public AbstractRod(String displayName, String namespaceName, Integer cooldown, List<String> baseLore) {
        this.displayName = displayName;
        this.namespaceName = namespaceName;
        this.cooldown = cooldown;
        this.baseLore = baseLore;
        this.persistentData = new HashMap<>();
        this.persistentDataTypes = new HashMap<>();

        addPersistentData(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT), 
                         PersistentDataType.INTEGER, 0);

        NamespacedKey key = new NamespacedKey(anendrod.getInstance(), namespaceName);
        this.recipe = new ShapelessRecipe(key, createItemStack());
    }
    
    public abstract void onUse(Player player, Player target);
    
    public abstract ItemStack createItemStack();
    
    //添加数据
    public <T, Z> void addPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.persistentData.put(key, value);
        this.persistentDataTypes.put(key, type);
    }
    
    //把数据写入Itemmeta
    protected void applyPersistentData(ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, Object> entry : persistentData.entrySet()) {
            NamespacedKey key = entry.getKey();
            PersistentDataType<?, ?> type = persistentDataTypes.get(key);
            Object value = entry.getValue();
            
            // 根据类型设置数据
            if (type == PersistentDataType.INTEGER) {
                container.set(key, PersistentDataType.INTEGER, (Integer) value);
            } else if (type == PersistentDataType.STRING) {
                container.set(key, PersistentDataType.STRING, (String) value);
            }
        }
    }
    
    // 从ItemMeta读取持久化数据
    protected void loadPersistentData(ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, PersistentDataType<?, ?>> entry : persistentDataTypes.entrySet()) {
            NamespacedKey key = entry.getKey();
            PersistentDataType<?, ?> type = entry.getValue();
            
            if (type == PersistentDataType.INTEGER && container.has(key, PersistentDataType.INTEGER)) {
                persistentData.put(key, container.get(key, PersistentDataType.INTEGER));
            } else if (type == PersistentDataType.STRING && container.has(key, PersistentDataType.STRING)) {
                persistentData.put(key, container.get(key, PersistentDataType.STRING));
            }
        }
    }
    
    public ShapelessRecipe getRecipe() {
        return recipe;
    }
    // 通用的事件处理方法
    @EventHandler
    public void onRodUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        
        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(this.displayName)) {
                event.setCancelled(true);
                
                if (!player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                    if (player.getEquipment().getLeggings() != null) {
                        player.sendMessage("§c他还穿着裤子！");
                        return;
                    }
                    handleRodUse(player, player, mainHand, meta);
                }
            }
        }
    }
    
    @EventHandler
    public void onRodUseToEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            ItemStack mainHand = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
            ItemMeta meta = mainHand.getItemMeta();
            
            if (meta != null && meta.getDisplayName().equals(this.displayName)) {
                event.setCancelled(true);
                
                if (player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                    if (target.getEquipment().getLeggings() != null) {
                        player.sendMessage("§c他还穿着裤子！");
                        return;
                    }
                    handleRodUse(player, target, mainHand, meta);
                }
            }
        }
    }
    //处理被()
    protected void handleRodUse(Player player, Player target, ItemStack item, ItemMeta meta) {
        //加载现有的持久化数据
        loadPersistentData(meta);
        //更新使用次数
        meta = useCounter.addTime(meta);
        
        //调用子类的自定义数据更新方法
        meta = updateItemData(meta);
        
        //重新应用持久化数据
        applyPersistentData(meta);
        //更新Lore
        meta = updateItemLore(meta);
        item.setItemMeta(meta);

        //这下看懂了
        onUse(player, target);
        player.setCooldown(Material.END_ROD, this.cooldown);
    }
    
    //如果需要更新其他的数据要重写这个
    protected ItemMeta updateItemData(ItemMeta meta) {
        return meta;
    }
    
    //子类可以重写此方法来更新Lore
    protected ItemMeta updateItemLore(ItemMeta meta) {
        int useCount = (Integer) persistentData.get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT));
        
        List<String> lore = new ArrayList<>(this.baseLore);
        lore.add("§7已使用 §e" + useCount + "§7 次");
        meta.setLore(lore);
        
        return meta;
    }
    protected ItemStack createBaseItemStack() {
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();
        
        meta.setDisplayName(this.displayName);
        
        List<String> lore = new ArrayList<>(this.baseLore);
        lore.add("§7已使用 §e0§7 次");
        meta.setLore(lore);

        applyPersistentData(meta);
        
        rod.setItemMeta(meta);
        return rod;
    }
}