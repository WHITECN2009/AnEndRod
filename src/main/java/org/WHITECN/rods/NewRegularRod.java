package org.WHITECN.rods;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.rodsHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class NewRegularRod extends AbstractRod {
    
    public NewRegularRod() {
        super(
            Vars.REGULAR_ROD_NAME,  // displayName
            "regular_rod",          // namespaceName
            10,                     // cooldown (ticks)
            Arrays.asList("§7没什么特别的 就是末地烛哦")  // baseLore
        );
        addRecipeIngredients();
    }
    
    @Override
    public void onUse(Player player, Player target) {
        rodsHandler.handleRegularRod(player, target);
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack rod = createBaseItemStack();
        ItemMeta meta = rod.getItemMeta();

        rod.setItemMeta(meta);
        return rod;
    }

    public void addRecipeIngredients() {
        getRecipe().addIngredient(1, Material.END_ROD);
    }
}