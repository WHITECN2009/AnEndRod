package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.utils.DeathStatus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class SlimeRod extends AbstractRod {

    public SlimeRod() {
        super(
            Vars.SLIME_ROD_NAME,
            "slime",
            10,
            Arrays.asList("§7一个黏糊糊的末地烛哦")
        );
    }
    
    @Override
    public void onUse(Player player, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1.0d);
        for (int i = 0; i < Math.abs(random.nextInt(2)); i++) {
            Slime entity = (Slime) target.getWorld().spawnEntity(target.getLocation(), EntityType.SLIME);
            entity.setCustomNameVisible(true);
            entity.setCustomName(ChatColor.LIGHT_PURPLE + target.getName() + "的末地烛的" + ChatColor.GREEN + "附着物");
            entity.setSize(1);
        }
        target.spawnParticle(Particle.HEART, player.getLocation(), 30, 1.5d, 1.0d, 1.5d);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));
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
        getRecipe().addIngredient(1, Material.END_ROD);
        getRecipe().addIngredient(1, Material.SLIME_BALL);
    }
}