package org.WHITECN.rods;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.AdvancementHandler;
import org.WHITECN.utils.SQLiteUtils;
import org.WHITECN.utils.tagUtils;
import org.WHITECN.utils.useCounter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class AbstractRod implements Listener {
    protected String displayName;
    protected String namespaceName;
    protected Integer cooldown;
    protected List<String> baseLore;
    protected ShapelessRecipe recipe;
    protected Map<NamespacedKey, Object> persistentData;
    protected Map<NamespacedKey, PersistentDataType<?, ?>> persistentDataTypes;
    protected Random random = new Random();
    protected List<Sound> Insert_sounds = Arrays.asList(
            Sound.ITEM_HONEYCOMB_WAX_ON,
            Sound.BLOCK_HONEY_BLOCK_HIT,
            Sound.BLOCK_HONEY_BLOCK_SLIDE,
            Sound.BLOCK_HONEY_BLOCK_STEP
    );
    
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

        addRecipeIngredients();
    }
    
    public abstract void onUse(Player player, Player target);
    
    public abstract ItemStack createItemStack();

    public abstract void addRecipeIngredients();
    
    //添加数据
    public <T, Z> void addPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.persistentData.put(key, value);
        this.persistentDataTypes.put(key, type);
    }
    

    protected void applyPersistentData(ItemMeta meta) {//把数据写入Itemmeta
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, Object> entry : persistentData.entrySet()) {
            NamespacedKey key = entry.getKey();
            PersistentDataType<?, ?> type = persistentDataTypes.get(key);
            Object value = entry.getValue();

            if (type == PersistentDataType.INTEGER) {
                container.set(key, PersistentDataType.INTEGER, (Integer) value);
            } else if (type == PersistentDataType.STRING) {
                container.set(key, PersistentDataType.STRING, (String) value);
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
                        player.sendTitle("","§c他还穿着裤子！");
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
                        player.sendTitle("","§c他还穿着裤子！");
                        return;
                    }
                    handleRodUse(player, target, mainHand, meta);
                }
            }
        }
    }
    //处理被()
    protected void handleRodUse(Player player, Player target, ItemStack item, ItemMeta meta) {
        meta = useCounter.addTime(meta);
        meta = updateItemData(meta); // <--- 如果需要就重写这个！！！
        meta = updateItemLore(meta); //Me too!
        item.setItemMeta(meta);

        //正片开始(bushi
        target.playSound(player, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        onUse(player, target);
        target.setNoDamageTicks(5);
        player.setCooldown(Material.END_ROD, this.cooldown);

        //---------存储数据---------

        // 性能优化：使用 PDC 存储统计数据，减少数据库频繁 IO
        tagUtils.ensureTag(target, "rodUsed", "0");
        int rodUsed = Integer.parseInt(tagUtils.getTag(target, "rodUsed")) + 1;
        tagUtils.setTag(target, "rodUsed", String.valueOf(rodUsed));
        AdvancementHandler.advancementTest(target);

        // 异步更新数据库统计，避免阻塞主线程
        String playerName = player.getName();
        String targetName = target.getName();
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(anendrod.class), () -> {
            SQLiteUtils.setCTCount(playerName, SQLiteUtils.getCTCount(playerName) + 1);
            SQLiteUtils.setChaCount(targetName, SQLiteUtils.getChaCount(targetName) + 1);
        });
        AdvancementHandler.advancementTest(target); //成就
    }
    
    //如果需要更新其他的数据要重写这个
    protected ItemMeta updateItemData(ItemMeta meta) {
        return meta;
    }
    //子类可以重写此方法来更新Lore
    protected ItemMeta updateItemLore(ItemMeta meta) {
        Integer useCount = meta.getPersistentDataContainer().get(
            new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT),
            PersistentDataType.INTEGER
        );
        
        if (useCount == null) {
            useCount = 0;
        }
        
        List<String> lore = new ArrayList<>(this.baseLore);
        lore.add("§7已使用 §e" + useCount + "§7 次");
        meta.setLore(lore);
        
        return meta;
    }
    public ItemStack createBaseItemStack() {
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();
        
        meta.setDisplayName(this.displayName);
        
        List<String> lore = new ArrayList<>(this.baseLore);
        meta.setLore(lore);

        applyPersistentData(meta);
        
        rod.setItemMeta(meta);
        return rod;
    }



}