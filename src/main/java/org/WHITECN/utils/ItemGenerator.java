package org.WHITECN.utils;

import net.md_5.bungee.api.ChatColor;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemGenerator {
    public static ItemStack createRegularRod() {
        // 创建基础物品
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();

        // 设置显示名称
        meta.setDisplayName(Vars.REGULAR_ROD_NAME);

        // 设置 Lore
        List<String> lore = new ArrayList<>();
        lore.add("§7没什么特别的 就是末地烛哦");
        lore.add("§7已使用 §e0 §7次");
        meta.setLore(lore);

        // 设置自定义NBT标签
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT),
                PersistentDataType.INTEGER, 0
        );

        meta.setCustomModelData(1);    //添加特殊数据，用于匹配材质包    

        // 应用修改
        rod.setItemMeta(meta);
        return rod;
    }
    public static ItemStack createSlimeRod() {
        // 创建基础物品
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();

        // 设置显示名称
        meta.setDisplayName(Vars.SLIME_ROD_NAME);

        // 设置 Lore
        List<String> lore = new ArrayList<>();
        lore.add("§7一个黏糊糊的末地烛哦\n");
        lore.add("§7已使用 §e0 §7次");
        meta.setLore(lore);

        // 设置自定义NBT标签
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT),
                PersistentDataType.INTEGER, 0
        );

        meta.setCustomModelData(2);    //添加特殊数据，用于匹配材质包    

        // 应用修改
        rod.setItemMeta(meta);
        return rod;
    }
    public static ItemStack createRegularProRod() {
        // 创建基础物品
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();

        // 设置显示名称
        meta.setDisplayName(Vars.PRO_ROD_NAME);
        //添加附魔效果
        meta.addEnchant(Enchantment.DURABILITY, 0, false);

        // 设置 Lore
        List<String> lore = new ArrayList<>();
        lore.add("§7普通末地烛的§bPro§7版");
        lore.add("§7已使用 §e0 §7次");
        meta.setLore(lore);

        // 设置自定义NBT标签
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT),
                PersistentDataType.INTEGER, 0
        );

        meta.setCustomModelData(3);    //添加特殊数据，用于匹配材质包

        // 应用修改
        rod.setItemMeta(meta);
        return rod;
    }
    public static ItemStack createPotionRod() {
        // 创建基础物品
        ItemStack rod = new ItemStack(Material.END_ROD);
        ItemMeta meta = rod.getItemMeta();

        // 设置显示名称
        meta.setDisplayName(Vars.POTION_ROD_NAME);
        //添加附魔效果
        meta.addEnchant(Enchantment.DURABILITY, 0, false);

        // 设置 Lore
        List<String> lore = Arrays.asList("§7可以沾药水的末地烛哦",
                "§7已使用 §e" + "0" + "§7 次",
                "§7上面还没有药水哦，把末地烛扔在地上用喷溅药水砸它试试？");
        meta.setLore(lore);

        // 设置自定义NBT标签
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT),
                PersistentDataType.INTEGER, 0
        );

        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION),
                PersistentDataType.STRING, ""
        );

        meta.setCustomModelData(4);    //添加特殊数据，用于匹配材质包

        // 应用修改
        rod.setItemMeta(meta);
        return rod;
    }
    public static ItemStack createHandCuffs(){
        ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d手铐♥");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "这是一个手铐，可以限制"
                + ChatColor.YELLOW + "玩家" + ChatColor.LIGHT_PURPLE + "挖掘速度和触碰距离...");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(),"code"),
                PersistentDataType.INTEGER_ARRAY, new int[] {0}
        );
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createKeyItem() {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7钥匙");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "这是一个钥匙，可以解锁也可以上锁");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                new NamespacedKey(anendrod.getInstance(),"code"),
                PersistentDataType.INTEGER_ARRAY, new int[] {0}
        );
        item.setItemMeta(meta);
        return item;

    }
}
