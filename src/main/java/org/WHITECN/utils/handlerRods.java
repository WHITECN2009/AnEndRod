package org.WHITECN.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class handlerRods {
    private static Random random = new Random();
    private static List<Sound> Insert_sounds = Arrays.asList(
        Sound.ITEM_HONEYCOMB_WAX_ON,
        Sound.BLOCK_HONEY_BLOCK_BREAK,
        Sound.BLOCK_HONEY_BLOCK_FALL,
        Sound.BLOCK_HONEY_BLOCK_HIT,
        Sound.BLOCK_HONEY_BLOCK_PLACE,
        Sound.BLOCK_HONEY_BLOCK_SLIDE,
        Sound.BLOCK_HONEY_BLOCK_STEP
    );
    public static void handlerRegularRod(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,4));
        player.damage(1.0d);
        player.setNoDamageTicks(0);
        player.playSound(player, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 100.0f, 1.0f);
        player.spawnParticle(Particle.HEART,player.getLocation(),30,1.5d,1.0d,1.5d);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));
        player.sendMessage(MessageGenerator.getRMessage());
    }
    public static void handlerSlimeRod(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,4));
        player.damage(1d);
        for (int i = 0; i < Math.abs(random.nextInt(5)); i++) {
            Slime entity = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME); entity.setSize(1);
        }
        player.setNoDamageTicks(0);
        player.playSound(player,Insert_sounds.get(random.nextInt(Insert_sounds.size())), 100.0f, 1.0f);
        player.spawnParticle(Particle.HEART,player.getLocation(),30,1.5d,1.0d,1.5d);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));
        player.sendMessage(MessageGenerator.getRMessage());
    }
}
