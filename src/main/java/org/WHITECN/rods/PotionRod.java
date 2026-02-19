package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.PotionUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

import static org.WHITECN.utils.rodsHandler.Insert_sounds;

public class PotionRod extends AbstractRod{
    public PotionRod(){
        super(
                Vars.POTION_ROD_NAME,  // displayName
                "potion_rod",          // namespaceName
                10,                     // cooldown (ticks)
                Arrays.asList("§7可以沾药水的末地烛哦")  // baseLore
        );
        addPersistentData(new NamespacedKey(anendrod.getInstance(),Vars.NAMESPACE_POTION),PersistentDataType.STRING,"");
    }
    @Override
    public void onUse(Player player, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1.0d);

        target.playSound(target, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        target.spawnParticle(Particle.HEART, target.getLocation(), 30, 1.5d, 1.0d, 1.5d);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d什么东西...?"));

        List<PotionEffect> effects = PotionUtils.parseString(player.getEquipment().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING)); //还可以更长呢！
        for (PotionEffect effect : effects) {
            target.addPotionEffect(effect);
        }
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack rod = createBaseItemStack();
        ItemMeta meta = rod.getItemMeta();
        meta = updateItemLore(meta);
        rod.setItemMeta(meta);
        return rod;
    }
    @Override
    public void addRecipeIngredients() {
        getRecipe().addIngredient(1,Material.END_ROD);
        getRecipe().addIngredient(1,Material.GLASS_BOTTLE);
    }

    //----------药水末地烛特有的

    @Override
    protected ItemMeta updateItemData(ItemMeta meta) {
        // 减少药水持续时间
        return PotionUtils.setPotion(meta);
    }

    @Override
    protected ItemMeta updateItemLore(ItemMeta meta) {
        // 先调用父类的方法显示基础信息和使用次数
        meta = super.updateItemLore(meta);
        
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new java.util.ArrayList<>();
        }
        
        // 检查是否有药水
        Boolean hasPotion = PotionUtils.hasPotion(meta);
        
        // 添加药水信息行
        lore.add(hasPotion ? "§e上面沾着这些药水：" : "§7上面还没有药水哦，把末地烛扔在地上用喷溅药水砸它试试？");
        
        // 如果有药水，显示具体效果
        if (hasPotion) {
            String potionData = PotionUtils.getPotion(meta);
            if (potionData != null && !potionData.isEmpty()) {
                java.util.List<org.bukkit.potion.PotionEffect> effects = PotionUtils.parseString(potionData);
                java.util.List<String> effectStrings = PotionUtils.toStringList(effects);
                lore.addAll(effectStrings);
            }
        }
        
        meta.setLore(lore);
        return meta;
    }
}
