package org.WHITECN.listeners;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.PotionUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiaoLuoDePenJianYaoshui implements Listener { //掉落的喷溅药水
    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        List<Entity> nearbyEntities = potion.getNearbyEntities(4.125, 4.125, 4.125);
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Item)) continue;
            Item item = (Item) entity;
            if (!item.getItemStack().getItemMeta().getDisplayName().equals(Vars.POTION_ROD_NAME)) continue;
            List<PotionEffect> potionEffects = new ArrayList<>(event.getPotion().getEffects());
            List<PotionEffect> IpotionEffects = PotionUtils.parseString(item.getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING, null)); //我才是最长的！
            for (int i = 0; i < potionEffects.size(); i++) {
                PotionEffect pe = potionEffects.get(i);
                if (pe.getAmplifier() < 1) {
                    potionEffects.set(i, new PotionEffect(pe.getType(), pe.getDuration(), 1, pe.isAmbient(), pe.hasParticles(), pe.hasIcon()));
                }
            }
            potionEffects.addAll(IpotionEffects);
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION),
                    PersistentDataType.STRING,
                    PotionUtils.getString(potionEffects)
            );
            Boolean hasPotion = PotionUtils.hasPotion(meta);
            List<String> lore = new ArrayList<>();
            lore.add("§7可以沾药水的末地烛哦");
            lore.add("§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT), PersistentDataType.INTEGER) + "§7 次");
            lore.add((hasPotion ? "§e上面沾着这些药水：" : "§7上面还没有药水哦，把末地烛扔在地上用喷溅药水砸它试试？"));
            if (hasPotion){
                lore.addAll(PotionUtils.toStringList(PotionUtils.parseString(PotionUtils.getPotion(meta))));
            }
            meta.setLore(lore);
            item.getItemStack().setItemMeta(meta);
            //System.out.println(item.getItemStack().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING));
        }
    }
}
