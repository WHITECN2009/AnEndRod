package org.WHITECN.utils;

import java.util.Objects;
import java.util.UUID;

import org.WHITECN.listeners.DeathListener;
import org.bukkit.inventory.ItemStack;

public class Status {

    private UUID player;
    private UUID neko;
    private double time;
    private ItemStack itemStack;

    /* ---------- 构造 ---------- */
    public Status() { }
    public Status(UUID player, UUID neko, double time) {
        this(player, neko, time, null);
    }
    public Status(UUID player, UUID neko, double time, ItemStack itemStack) {
        this.player = player;
        this.neko = neko;
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

    public UUID getNeko() {
        return neko;
    }

    public void setNeko(UUID neko) {
        this.neko = neko;
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
               ", neko=" + neko +
               ", time=" + time +
               ", itemStack=" + itemStack +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status = (Status) o;
        return Double.compare(status.time, time) == 0 &&
               Objects.equals(player, status.player) &&
               Objects.equals(neko, status.neko) &&
               Objects.equals(itemStack, status.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, neko, time, itemStack);
    }
    public static void add(UUID player, UUID neko, double time, ItemStack itemStack) {
        DeathListener.mStatus.compute(player, (k, old) -> {
            if (old == null) {
                return new Status(player, neko, time, itemStack);
            }
            // 已存在就原地刷新喵
            old.setPlayer(player);
            old.setNeko(neko);
            old.setTime(time);
            old.setItemStack(itemStack);
            return old;
        });
    }
}