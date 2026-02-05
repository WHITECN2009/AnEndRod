package org.WHITECN.utils;

import java.util.Objects;
import java.util.UUID;

import org.WHITECN.listeners.DeathListener;
import org.bukkit.inventory.ItemStack;

public class DeathStatus {

    private UUID player;
    private UUID target;
    private double time;
    private ItemStack itemStack;

    /* ---------- 构造 ---------- */
    public DeathStatus() { }
    public DeathStatus(UUID player, UUID target, double time) {
        this(player, target, time, null);
    }
    public DeathStatus(UUID player, UUID target, double time, ItemStack itemStack) {
        this.player = player;
        this.target = target;
        this.time = time;
        this.itemStack = itemStack;
    }

    /* ---------- getter / setter ---------- */
    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    @Override
    public String toString() {
        return "Status{" +
               "player=" + player +
               ", target=" + target +
               ", time=" + time +
               ", itemStack=" + itemStack +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeathStatus)) return false;
        DeathStatus status = (DeathStatus) o;
        return Double.compare(status.time, time) == 0 &&
               Objects.equals(player, status.player) &&
               Objects.equals(target, status.target) &&
               Objects.equals(itemStack, status.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, target, time, itemStack);
    }
    public static void add(UUID player, UUID target, double time, ItemStack itemStack) {
        // 使用 target 作为 Key，因为死亡事件是针对 target (受害者) 触发的
        DeathListener.mStatus.compute(target, (k, old) -> {
            if (old == null) {
                return new DeathStatus(player, target, time, itemStack);
            }
            // 已存在就原地刷新喵
            old.setPlayer(player);
            old.setTarget(target);
            old.setTime(time);
            old.setItemStack(itemStack);
            return old;
        });
    }
}