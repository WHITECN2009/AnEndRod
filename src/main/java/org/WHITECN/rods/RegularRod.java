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
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

import static org.WHITECN.utils.rodsHandler.Insert_sounds;

public class RegularRod extends AbstractRod {

    public RegularRod() {
        super(
            Vars.REGULAR_ROD_NAME,  // displayName
            "regular_rod",          // namespaceName
            10,                     // cooldown (ticks)
            Arrays.asList("§7没什么特别的 就是末地烛哦")  // baseLore
        );
    }
    
    @Override
    public void onUse(Player player, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1.0d);
        target.playSound(target, Insert_sounds.get(this.random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        target.spawnParticle(Particle.HEART, target.getLocation(), 30, 1.5d, 1.0d, 1.5d);
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
    }
}