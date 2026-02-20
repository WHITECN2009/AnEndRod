package org.WHITECN.rods;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.AdvancementHandler;
import org.WHITECN.utils.SQLiteUtils;
import org.WHITECN.utils.rodsHandler;
import org.WHITECN.utils.tagUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
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
        
        rodsHandler.handleSlimeRod(player, target);
        
        org.WHITECN.utils.DeathStatus.add(player.getUniqueId(), target.getUniqueId(), 10*20, player.getEquipment().getItemInMainHand());
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