package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.*;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PotionRod implements Listener {

    @EventHandler
    public void onPotionRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();

        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(Vars.POTION_ROD_NAME)) {
                event.setCancelled(true);

                if (!player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                    mainHand.setItemMeta(useCounter.addTime(meta));
                    mainHand.setItemMeta(PotionUtils.setPotion(meta));

                    Boolean hasPotion = PotionUtils.hasPotion(meta);

                    List<String> lore = new ArrayList<>();
                    lore.add("§7可以沾药水的末地烛哦");
                    lore.add("§7已使用 §e" + meta.getPersistentDataContainer()
                            .get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT), PersistentDataType.INTEGER) + "§7 次");
                    lore.add(hasPotion ? "§e上面沾着这些药水：" : "§7上面还没有药水哦，把末地烛扔在地上用喷溅药水砸它试试？");
                    if (hasPotion) {
                        String potionData = PotionUtils.getPotion(meta);
                        if (potionData != null && !potionData.isEmpty()) {
                            List<PotionEffect> effects = PotionUtils.parseString(potionData);
                            if (effects != null) {
                                effects = new ArrayList<>(effects);
                                List<String> effectStrings = PotionUtils.toStringList(effects);
                                if (effectStrings != null) {
                                    lore.addAll(new ArrayList<>(effectStrings));
                                }
                            }
                        }
                    }

                    meta.setLore(lore);
                    mainHand.setItemMeta(meta);

                    DeathStatus.add(player.getUniqueId(), player.getUniqueId(), 10, mainHand);
                    rodsHandler.handlePotionRod(player, player, meta);
                }
            }
        }
    }

    @EventHandler
    public void onPotionRod_toEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            ItemStack mainHand = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
            ItemMeta meta = mainHand.getItemMeta();

            if (meta != null && meta.getDisplayName().equals(Vars.POTION_ROD_NAME)) {
                event.setCancelled(true);

                if (player.isSneaking() && player.getCooldown(Material.END_ROD) == 0) {
                    if (target.getEquipment().getLeggings() != null) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c怎么穿着裤子喵!"));
                        return;
                    }

                    mainHand.setItemMeta(PotionUtils.setPotion(meta));
                    mainHand.setItemMeta(useCounter.addTime(meta));

                    Boolean hasPotion = PotionUtils.hasPotion(meta);

                    // === 修复3：同样的修复在这里 ===
                    List<String> lore = new ArrayList<>();
                    lore.add("§7可以沾药水的末地烛哦");
                    lore.add("§7已使用 §e" + meta.getPersistentDataContainer()
                            .get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_COUNT), PersistentDataType.INTEGER) + "§7 次");
                    lore.add(hasPotion ? "§e上面沾着这些药水：" : "§7上面还没有药水哦，把末地烛扔在地上用喷溅药水砸它试试？");

                    if (hasPotion) {
                        String potionData = PotionUtils.getPotion(meta);
                        if (potionData != null && !potionData.isEmpty()) {
                            List<PotionEffect> effects = new ArrayList<>(PotionUtils.parseString(potionData));
                            List<String> effectStrings = new ArrayList<>(PotionUtils.toStringList(effects));
                            lore.addAll(effectStrings);
                        }
                    }

                    meta.setLore(lore);
                    mainHand.setItemMeta(meta);

                    DeathStatus.add(player.getUniqueId(), target.getUniqueId(), 10, mainHand);
                    rodsHandler.handlePotionRod(event.getPlayer(), target, meta);
                }
            }
        }
    }
}