package org.WHITECN;

import org.WHITECN.commands.rodMerge;
import org.WHITECN.rods.RegularRod;
import org.WHITECN.rods.SlimeRod;
import org.WHITECN.utils.rodItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class anendrod extends JavaPlugin {
    private static anendrod instance;
    private static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.info("插件已启用喵");
        getServer().getPluginManager().registerEvents(new SlimeRod(),this);
        getServer().getPluginManager().registerEvents(new RegularRod(),this);
        Objects.requireNonNull(this.getCommand("rodmerge")).setExecutor(new rodMerge(this));
        registerSlimeRodRecipes();
    }

    @Override
    public void onDisable() {
        logger.info("插件已禁用喵");
    }

    public static anendrod getInstance() {
        return instance;
    }
    private void registerSlimeRodRecipes() {
        ItemStack SlimeRod = rodItemGenerator.createSlimeRod();
        NamespacedKey key = new NamespacedKey(this, "slime_rod");
        
        ShapelessRecipe recipe = new ShapelessRecipe(key, SlimeRod);
        recipe.addIngredient(Material.END_ROD);
        recipe.addIngredient(8,Material.SLIME_BALL);
        
        Bukkit.addRecipe(recipe);
    }
}
