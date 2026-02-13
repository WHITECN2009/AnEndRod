package org.WHITECN.utils;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class useCounter {
    public static ItemMeta addTime(ItemMeta meta){
        anendrod plugin = anendrod.getInstance();
        NamespacedKey usedTimeKey = new NamespacedKey(plugin, Vars.NAMESPACE_COUNT);
        if (meta.getPersistentDataContainer().has(usedTimeKey, PersistentDataType.INTEGER)) {
            int usedTime = meta.getPersistentDataContainer().get(usedTimeKey, PersistentDataType.INTEGER);
            meta.getPersistentDataContainer().set(usedTimeKey, PersistentDataType.INTEGER, usedTime + 1);
            return meta;
        }
        return meta;
    }
}
