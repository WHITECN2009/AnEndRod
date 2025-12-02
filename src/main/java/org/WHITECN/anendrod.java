package org.WHITECN;

import org.WHITECN.commands.rodMerge;
import org.WHITECN.rods.RegularProRod;
import org.WHITECN.rods.RegularRod;
import org.WHITECN.rods.SlimeRod;
import org.WHITECN.utils.rodItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

public final class anendrod extends JavaPlugin {
    private static anendrod instance;
    private static Logger logger;
    private static final String PACK_URL = "https://bgithub.xyz/WHITECN2009/AnEndRod/raw/refs/heads/master/src/main/resources/AnEndRod_Pack.zip";

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.info("插件已启用喵");
        Objects.requireNonNull(this.getCommand("rodmerge")).setExecutor(new rodMerge(this));
        getServer().getPluginManager().registerEvents(new SlimeRod(),this);
        getServer().getPluginManager().registerEvents(new RegularRod(),this);
        getServer().getPluginManager().registerEvents(new RegularProRod(),this);

        saveResource("AnEndRod_Pack.zip", true);

        //此处注册配方变量
        NamespacedKey regular = new NamespacedKey(anendrod.getInstance(),"regular_rod");
        ShapelessRecipe regularRod = new ShapelessRecipe(regular, rodItemGenerator.createRegularRod());
        NamespacedKey slime = new NamespacedKey(anendrod.getInstance(),"slime");
        ShapelessRecipe slimeRod = new ShapelessRecipe(slime,rodItemGenerator.createSlimeRod());
        NamespacedKey pro = new NamespacedKey(anendrod.getInstance(),"pro");
        ShapelessRecipe proRod = new ShapelessRecipe(pro,rodItemGenerator.createRegularProRod());

        //此处注册配方物品
        regularRod.addIngredient(1, Material.END_ROD);
        slimeRod.addIngredient(1,Material.END_ROD);
        slimeRod.addIngredient(1,Material.SLIME_BALL);

        proRod.addIngredient(9,Material.END_ROD);
        //此处注册配方
        getServer().addRecipe(regularRod);
        getServer().addRecipe(slimeRod);
        getServer().addRecipe(proRod);

        //配方解锁方法
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                getServer().getScheduler().runTaskLater(instance, () -> {
                    if (event.getPlayer().isOnline()) {
                        event.getPlayer().discoverRecipes(Collections.singletonList(regular));
                        event.getPlayer().discoverRecipes(Collections.singletonList(slime));
                        event.getPlayer().discoverRecipes(Collections.singletonList(pro));
                        event.getPlayer().setResourcePack(PACK_URL);//材质包
                    }
                }, 20L);
            }
        }, this);
        for(Player p : Bukkit.getOnlinePlayers()){
            p.discoverRecipes(Collections.singletonList(regular));
            p.discoverRecipes(Collections.singletonList(slime));
            p.discoverRecipes(Collections.singletonList(pro));
        }//为在线猫粮注册配方
    }

    @Override
    public void onDisable() {
        logger.info("插件已禁用喵");
    }

    public static anendrod getInstance() {
        return instance;
    }
}
