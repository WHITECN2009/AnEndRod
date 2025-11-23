package org.WHITECN;

import org.WHITECN.commands.rodMerge;
import org.WHITECN.rods.RegularRod;
import org.WHITECN.rods.SlimeRod;
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
    }

    @Override
    public void onDisable() {
        logger.info("插件已禁用喵");
    }

    public static anendrod getInstance() {
        return instance;
    }
}
