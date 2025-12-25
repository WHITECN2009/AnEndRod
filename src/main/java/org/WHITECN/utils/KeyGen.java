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
            
    private static String getKeyShape(List<Integer> code) {
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
    public static ItemMeta setKey(ItemMeta meta) {
        anendrod plugin = anendrod.getInstance();
        NamespacedKey theKey = new NamespacedKey(plugin, "the_key");
        int[] array = genKey().stream().mapToInt(Integer::intValue).toArray();
        meta.getPersistentDataContainer().set(theKey, PersistentDataType.INTEGER_ARRAY, array);
        return meta;
    }

    public static List<Integer> genKey() {
        List<Integer> key = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) key.add(RANDOM.nextInt(10));
        return key;
    }

    public static List<Integer> getKey(ItemMeta meta) {
        anendrod plugin = anendrod.getInstance();
        NamespacedKey theKey = new NamespacedKey(plugin, "the_key");
        int[] array = meta.getPersistentDataContainer().get(theKey, PersistentDataType.INTEGER_ARRAY);
        if (array == null || array.length != 10) return List.of();
        List<Integer> list = new ArrayList<>(10);
        for (int v : array) list.add(v);
        return list;
    }
}