package org.WHITECN;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.WHITECN.utils.SQLiteUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return "N501YHappy";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "anendrod";
    }

    @Override
    public @NotNull String getVersion() {
        return anendrod.getInstance().getDescription().getVersion();  // 修正：改为AnEndRod
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (!offlinePlayer.isOnline()) {
            return "§c离线";
        }

        Player player = offlinePlayer.getPlayer();
        if (player == null) return "0";
        int player_cha = SQLiteUtils.getChaCount(player.getName());
        int player_chato = SQLiteUtils.getCTCount(player.getName());
        double player_kd = (player_cha == 0) ? player_chato : player_chato * 1.0 / player_cha;

        switch (params.toLowerCase()) {
            case "cha":
                return String.valueOf(player_cha);
            case "cha_format":
                return formatter(player_cha);
            case "chato":
                return String.valueOf(player_chato);
            case "chato_format":
                return formatter(player_chato);
            case "kd":
                return getColoredKD(player_kd);
            default:
                return null;
        }
    }

    private String formatter(int x) {
        if (x < 1000) {
            return String.valueOf(x);
        } else if (x < 1000000) {
            return String.format("%.2fk", x / 1000.0);
        } else if (x < 1000000000) {
            return String.format("%.2fM", x / 1000000.0);
        } else {
            return String.format("%.2fB", x / 1000000000.0);
        }
    }

    private String getColoredKD(double kd) {
        String kdStr = String.format("%.2f", kd);

        if (kd >= 500.0) {
            return "§d§l" + kdStr;  // 粉色加粗
        } else if (kd >= 300.0) {
            return "§c" + kdStr;    // 绿色
        } else if (kd >= 100) {
            return "§e" + kdStr;    // 黄色
        } else {
            return "§a" + kdStr;    // 红色
        }
    }
}