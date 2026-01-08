package org.WHITECN;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;

import org.WHITECN.commands.rodMerge;
import org.WHITECN.items.HandcuffsAndKey;
import org.WHITECN.listeners.DeathListener;
import org.WHITECN.rods.RegularProRod;
import org.WHITECN.rods.RegularRod;
import org.WHITECN.rods.SlimeRod;
import org.WHITECN.runnables.DeathRunnable;
import org.WHITECN.runnables.HandcuffsRunnable;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.ItemGenerator;
import org.WHITECN.utils.SQLiteUtils;
import org.WHITECN.utils.tagUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class anendrod extends JavaPlugin {
    private static anendrod instance;
    private static Logger logger;
    public static final String prefix = "§d[EndRod]§r ";
    private Placeholders placeholders;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        logger.info("插件已启用喵");
        SQLiteUtils.init(this); //初始化数据库utils
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("P!A!P!I!");
            placeholders = new Placeholders();
            if (placeholders.register()) {
                getLogger().info("PAPI占位符注册成功！");
            } else {
                getLogger().warning("PAPI占位符注册失败！");
            }
        } else {
            getLogger().warning("或许有了PAPI会更好？");
        }
        Objects.requireNonNull(this.getCommand("rodmerge")).setExecutor(new rodMerge(this));
        getServer().getPluginManager().registerEvents(new SlimeRod(),this);
        getServer().getPluginManager().registerEvents(new RegularRod(),this);
        getServer().getPluginManager().registerEvents(new RegularProRod(),this);
        getServer().getPluginManager().registerEvents(new DeathListener(this),this);
        getServer().getPluginManager().registerEvents(new HandcuffsAndKey(),this);

        saveResource("AnEndRod_Pack.zip", true);
        ConfigManager.loadConfig(this); //加载配置文件
        tagUtils.init(this);

        // 移除已存在的配方（防止重复注册）
        removeRecipeIfExists("regular_rod");
        removeRecipeIfExists("slime");
        removeRecipeIfExists("pro");
        removeRecipeIfExists("handcuff");
        removeRecipeIfExists("key");

        //此处注册配方变量
        NamespacedKey regular = new NamespacedKey(anendrod.getInstance(),"regular_rod");
        ShapelessRecipe regularRod = new ShapelessRecipe(regular, ItemGenerator.createRegularRod());
        NamespacedKey slime = new NamespacedKey(anendrod.getInstance(),"slime");
        ShapelessRecipe slimeRod = new ShapelessRecipe(slime, ItemGenerator.createSlimeRod());
        NamespacedKey pro = new NamespacedKey(anendrod.getInstance(),"pro");
        ShapelessRecipe proRod = new ShapelessRecipe(pro, ItemGenerator.createRegularProRod());
        NamespacedKey handcuff = new NamespacedKey(anendrod.getInstance(),"handcuff");
        ShapelessRecipe handcuffItem = new ShapelessRecipe(handcuff,ItemGenerator.createHandCuffs());
        NamespacedKey key = new NamespacedKey(anendrod.getInstance(),"key");
        ShapelessRecipe keyItem = new ShapelessRecipe(key,ItemGenerator.createKeyItem());

        //此处注册配方物品
        regularRod.addIngredient(1, Material.END_ROD);
        slimeRod.addIngredient(1,Material.END_ROD);
        slimeRod.addIngredient(1,Material.SLIME_BALL);
        proRod.addIngredient(9,Material.END_ROD);
        handcuffItem.addIngredient(2,Material.IRON_INGOT);
        handcuffItem.addIngredient(2,Material.CHAIN);
        keyItem.addIngredient(1,Material.IRON_INGOT);
        keyItem.addIngredient(1,Material.STICK);

        //此处注册配方

        getServer().addRecipe(regularRod);
        getServer().addRecipe(slimeRod);
        getServer().addRecipe(proRod);
        getServer().addRecipe(handcuffItem);
        getServer().addRecipe(keyItem);

        //配方解锁方法和确保玩家标签
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                getServer().getScheduler().runTaskLater(instance, () -> {
                    if (event.getPlayer().isOnline()) {
                        event.getPlayer().discoverRecipes(Collections.singletonList(regular));
                        event.getPlayer().discoverRecipes(Collections.singletonList(slime));
                        event.getPlayer().discoverRecipes(Collections.singletonList(handcuff));
                        event.getPlayer().discoverRecipes(Collections.singletonList(pro));
                        if (ConfigManager.ENABLE_PACK) {
                            event.getPlayer().setResourcePack(ConfigManager.PACK_URL);//材质包
                        }
                        tagUtils.ensureTag(event.getPlayer(),"rodUsed","0");
                    }
                }, 20L);
            }
        }, this);
        for(Player player : Bukkit.getOnlinePlayers()){
            player.discoverRecipes(Collections.singletonList(regular));
            player.discoverRecipes(Collections.singletonList(slime));
            player.discoverRecipes(Collections.singletonList(pro));
            player.discoverRecipes(Collections.singletonList(handcuff));
            player.discoverRecipes(Collections.singletonList(key));
        }//为在线猫粮注册配方
        new DeathRunnable().runTaskTimer(this, 0L, 20L); //计时器！！！
        new HandcuffsRunnable().runTaskTimer(this, 0L, 20L); //计时器！！！
    }

    @Override
    public void onDisable() {
        logger.info("插件已禁用喵");
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
        Iterator<Recipe> iterator = getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe instanceof Keyed) {
                if (((Keyed) recipe).getKey().equals(key)) {
                    iterator.remove();
                    Bukkit.getConsoleSender().sendMessage("§a[AnEndRod] §f已移除旧配方: " + recipeKey);
                    return;
                }
            }
        }
    }
}
