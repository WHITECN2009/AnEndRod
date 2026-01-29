package org.WHITECN.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SlimesListener implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent event){
        if (event.getEntityType() != EntityType.SLIME) return;
        LivingEntity entity = event.getEntity();
        if (entity.getCustomName().contains("附着物")){
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}
