package org.WHITECN.utils;

import org.WHITECN.anendrod;
import org.WHITECN.Vars;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class rodItemGenerator {
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
                new NamespacedKey(anendrod.getInstance(), "useCount"),
                PersistentDataType.INTEGER, 0
        );

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
                new NamespacedKey(anendrod.getInstance(), "useCount"),
                PersistentDataType.INTEGER, 0
        );

        // 应用修改
        rod.setItemMeta(meta);
        return rod;
    }
}
