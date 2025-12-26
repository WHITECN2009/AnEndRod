package org.WHITECN.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.WHITECN.anendrod;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class KeyGen {
    public static final Random RANDOM = new Random();
    private static final List<Character> KEY_CHARS =
            Arrays.asList('╦', '╬', '╩', '╦', '╬', '╩', '╦', '╬', '╩', '╦');
            
    public static String getKeyShape(List<Integer> code) {
        if (code == null || code.size() != 10) {
            throw new IllegalArgumentException("需要 10 位数字");
        }
        
        StringBuilder sb = new StringBuilder(11);
        sb.append('◯');

        for (int digit : code) {
            sb.append(KEY_CHARS.get(digit));
        }
        return sb.toString();
    }

    // 游戏逻辑用 List<Integer>
    public static ItemMeta setKey(ItemMeta meta, List<Integer> code) {
        int[] arr = code.stream().mapToInt(Integer::intValue).toArray();
        return setKey(meta, arr);
    }

    // NBT 存储用 int[]
    public static ItemMeta setKey(ItemMeta meta, int[] newCode) {
        NamespacedKey codeKey = new NamespacedKey(anendrod.getInstance(), "code");
        meta.getPersistentDataContainer().set(codeKey, PersistentDataType.INTEGER_ARRAY, newCode);
        return meta;
    }

    public static List<Integer> generateKey() {
        List<Integer> key = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) key.add(RANDOM.nextInt(10));
        return key;
    }

    public static List<Integer> getKey(ItemMeta meta) {
        anendrod plugin = anendrod.getInstance();
        NamespacedKey codeKey = new NamespacedKey(plugin, "code");
        int[] array = meta.getPersistentDataContainer().get(codeKey, PersistentDataType.INTEGER_ARRAY);
        if (array == null || array.length != 10) return List.of();
        List<Integer> list = new ArrayList<>(10);
        for (int v : array) list.add(v);
        return list;
    }
}