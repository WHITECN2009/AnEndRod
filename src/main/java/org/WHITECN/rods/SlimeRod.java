package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.anendrod;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.WHITECN.utils.rodItemGenerator;
import org.WHITECN.utils.useCounter;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class SlimeRod implements Listener {
    private Random random = new Random();
    @EventHandler
    public void onRegularRod(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        if (mainHand != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(rodItemGenerator.createSlimeRod().getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                mainHand.setItemMeta(useCounter.addTime(meta));
                meta.setLore(List.of("§7一个黏糊糊的末地烛哦\n","§7已使用 §e" + meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(),"useCount"), PersistentDataType.INTEGER) + "§7 次"));
                mainHand.setItemMeta(meta);
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,5*20,4));
                player.damage(1d);
                player.setVelocity(new Vector(0, 0.25, 0));
                for (int i = 0; i < Math.abs(random.nextInt(5)) + 1; i++) {
                    Slime entity = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME); entity.setSize(1);
                }
                player.setNoDamageTicks(0);
                player.playSound(player, Sound.BLOCK_HONEY_BLOCK_SLIDE, 100.0f, 1.0f);
                player.spawnParticle(Particle.HEART,player.getLocation(),30,1.5d,1.0d,1.5d);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));
            }
        }
    }
}
