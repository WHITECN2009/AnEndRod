package org.WHITECN;

import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

import org.WHITECN.commands.rodMerge;
import org.WHITECN.items.HandcuffsAndKey;
import org.WHITECN.listeners.DeathListener;
import org.WHITECN.listeners.DroppedSplashPotion;
import org.WHITECN.rods.*;
import org.WHITECN.runnables.DeathRunnable;
import org.WHITECN.runnables.HandcuffsRunnable;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.ItemGenerator;
import org.WHITECN.utils.SQLiteUtils;
import org.WHITECN.utils.tagUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class anendrod extends JavaPlugin {
    private static anendrod instance;
    private static Logger logger;
    public static final String prefix = "§7[§eEnd§dRod§7]§r ";
    private Placeholders placeholders;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        int pluginId = 29492;
        Metrics metrics = new Metrics(this, pluginId);

        logger.info("插件已启用喵");
        SQLiteUtils.init(this); //初始化数据库utils
        Objects.requireNonNull(this.getCommand("rodmerge")).setExecutor(new rodMerge(this));
        getServer().getPluginManager().registerEvents(new SlimeRod(),this);
        getServer().getPluginManager().registerEvents(new RegularRod(),this);
        getServer().getPluginManager().registerEvents(new RegularProRod(),this);
        getServer().getPluginManager().registerEvents(new PotionRod(),this);
        getServer().getPluginManager().registerEvents(new DeathListener(this),this);
        getServer().getPluginManager().registerEvents(new HandcuffsAndKey(),this);
        getServer().getPluginManager().registerEvents(new DroppedSplashPotion(),this);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginEnable(PluginEnableEvent event) {
                if (event.getPlugin().getName().equalsIgnoreCase("PlaceholderAPI")) {
                    getLogger().info("PAPI...");
                    if (placeholders != null && placeholders.isRegistered()) {
                        placeholders.unregister();
                    }
                    placeholders = new Placeholders();
                    if (placeholders.register()) {
                        getLogger().info("P!A!P!I!");
                    }
                }
            }
        }, this);
        saveResource("AnEndRod_Pack.zip", true);
        ConfigManager.loadConfig(this); //加载配置文件
        tagUtils.init(this);

        // 移除已存在的配方（防止重复注册）
        removeRecipeIfExists("regular_rod");
        removeRecipeIfExists("slime");
        removeRecipeIfExists("pro");
        removeRecipeIfExists("potion");
        removeRecipeIfExists("handcuff");
        removeRecipeIfExists("key");

        RegularRod regularRod = new RegularRod();
        getServer().addRecipe(regularRod.getRecipe());
        
        PotionRod potionRod = new PotionRod();
        getServer().addRecipe(potionRod.getRecipe());
        
        RegularProRod regularProRod = new RegularProRod();
        getServer().addRecipe(regularProRod.getRecipe());
        
        SlimeRod slimeRod = new SlimeRod();
        getServer().addRecipe(slimeRod.getRecipe());

        //此处注册配方变量

        NamespacedKey handcuff = new NamespacedKey(anendrod.getInstance(),"handcuff");
        ShapelessRecipe handcuffItem = new ShapelessRecipe(handcuff,ItemGenerator.createHandCuffs());
        NamespacedKey key = new NamespacedKey(anendrod.getInstance(),"key");
        ShapelessRecipe keyItem = new ShapelessRecipe(key,ItemGenerator.createKeyItem());

        //此处注册配方物品
        handcuffItem.addIngredient(2,Material.IRON_INGOT);
        handcuffItem.addIngredient(2,Material.CHAIN);
        keyItem.addIngredient(1,Material.IRON_INGOT);
        keyItem.addIngredient(1,Material.STICK);

        //此处注册配方

        getServer().addRecipe(handcuffItem);
        getServer().addRecipe(keyItem);

        //配方解锁方法和确保玩家标签
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                getServer().getScheduler().runTaskLater(instance, () -> {
                    if (event.getPlayer().isOnline()) {
                        event.getPlayer().discoverRecipes(Collections.singletonList(regularRod.getRecipe().getKey()));
                        event.getPlayer().discoverRecipes(Collections.singletonList(potionRod.getRecipe().getKey()));
                        event.getPlayer().discoverRecipes(Collections.singletonList(regularProRod.getRecipe().getKey()));
                        event.getPlayer().discoverRecipes(Collections.singletonList(slimeRod.getRecipe().getKey()));
                        event.getPlayer().discoverRecipes(Collections.singletonList(handcuff));
                        if (ConfigManager.ENABLE_PACK) {
                            event.getPlayer().setResourcePack(ConfigManager.PACK_URL);
                        }
                        tagUtils.ensureTag(event.getPlayer(),"rodUsed","0");
                    }
                }, 20L);
            }
        }, this);
        for(Player player : Bukkit.getOnlinePlayers()){
            player.discoverRecipes(Collections.singletonList(regularRod.getRecipe().getKey()));
            player.discoverRecipes(Collections.singletonList(potionRod.getRecipe().getKey()));
            player.discoverRecipes(Collections.singletonList(regularProRod.getRecipe().getKey()));
            player.discoverRecipes(Collections.singletonList(slimeRod.getRecipe().getKey()));
            player.discoverRecipes(Collections.singletonList(handcuff));
            player.discoverRecipes(Collections.singletonList(key));
        }
        new DeathRunnable().runTaskTimer(this, 0L, 20L); //计时器！！！
        new HandcuffsRunnable().runTaskTimer(this, 0L, 20L); //计时器！！！
    }

    @Override
    public void onDisable() {
        logger.info("插件已禁用喵");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) return;
        if (placeholders != null) {
            placeholders.unregister();
        }
    }

    public static anendrod getInstance() {
        return instance;
    }

    /**
     * 移除已存在的配方
     * @param recipeKey 配方键名
     */
    private void removeRecipeIfExists(String recipeKey) {
        NamespacedKey key = new NamespacedKey(this, recipeKey);
        try {
            // 混合端中可能存在部分 Mod 注册了非 3x3 的特殊配方
            // 使用 Iterator 遍历会导致 Bukkit 校验配方行数时抛出 IllegalArgumentException
            // 直接通过 NamespacedKey 移除避免校验
            getServer().removeRecipe(key);
            Bukkit.getConsoleSender().sendMessage("§a[AnEndRod] §f已成功移除配方: " + recipeKey);
        } catch (Exception e) {
            // 如果配方不存在或该环境不支持直接移除，无需处理
        }
    }
}
