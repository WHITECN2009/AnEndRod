package org.WHITECN.utils;

import org.bukkit.plugin.Plugin;


public class ConfigManager {
    public static String PACK_URL;
    public static Boolean SUMMON_SLIME;
    public static Boolean ENABLE_PACK;
    public static Boolean PRO_SPAWN_WATER;
    public static void loadConfig(Plugin plugin){
        plugin.saveDefaultConfig();
        plugin.saveResource("AnEndRod_Pack.zip", true);
        plugin.reloadConfig(); //一定要先reload一下喵！
        ENABLE_PACK = plugin.getConfig().getBoolean("enable_pack",true);//是否开启材质包
        SUMMON_SLIME = plugin.getConfig().getBoolean("summon_slime",true);//是否开启生成史莱姆
        PRO_SPAWN_WATER = plugin.getConfig().getBoolean("pro_spawn_water",true);//是否开启Pro生成水方块
        PACK_URL = plugin.getConfig().getString("pack_url","https://bgithub.xyz/WHITECN2009/AnEndRod/raw/refs/heads/master/src/main/resources/AnEndRod_Pack.zip");
    }
}
