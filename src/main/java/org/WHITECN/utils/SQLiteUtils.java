package org.WHITECN.utils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteUtils {
    public static Connection c = null;
    public static Plugin plugin = null;

    public static void init(Plugin plugin){
        SQLiteUtils.plugin = plugin;
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dbFile = new File(dataFolder, "nekodata.db");
            String dbPath = dbFile.getAbsolutePath();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            plugin.getLogger().info("Connected! " + dbPath);
            create_Table();

        } catch (SQLException e) {
            plugin.getLogger().severe("数据库连接失败: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite驱动未找到: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void create_Table(){
        Statement statement = null;
        try {
            statement = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS cha_count ("
                    + "Player TEXT PRIMARY KEY, "
                    + "Count INTEGER DEFAULT 0"
                    + ")";
            statement.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS cha_to_count ("
                    + "Player TEXT PRIMARY KEY, "
                    + "Count INTEGER DEFAULT 0"
                    + ")";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void setChaCount(String player, int count){
        String sql = "INSERT OR REPLACE INTO cha_count (Player, Count) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, count);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int getChaCount(String player){
        String sql = "SELECT Count FROM cha_count WHERE Player = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, player);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return 0; //如果没有的话
    }

    public static void setCTCount(String player, int count){
        String sql = "INSERT OR REPLACE INTO cha_to_count (Player, Count) VALUES (?, ?)";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, count);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int getCTCount(String player){
        String sql = "SELECT Count FROM cha_to_count WHERE Player = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, player);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static List<String> getTOPC(int n){
        List<String> result = new ArrayList<>();
        String sql = "SELECT Player FROM cha_count ORDER BY Count DESC LIMIT ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("Player"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<String> getTopPlayersFromCTCount(int n){
        List<String> result = new ArrayList<>();
        String sql = "SELECT Player FROM cha_to_count ORDER BY Count DESC LIMIT ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("Player"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<String> getTopPlayersWithCountFromChaCount(int n){
        List<String> result = new ArrayList<>();
        String sql = "SELECT Player, Count FROM cha_count ORDER BY Count DESC LIMIT ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("Player") + ": " + rs.getInt("Count"));
            }
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<String> getTOPCT(int n){
        List<String> result = new ArrayList<>();
        String sql = "SELECT Player, Count FROM cha_to_count ORDER BY Count DESC LIMIT ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("Player") + ": " + rs.getInt("Count"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void clearTable(String tableName){
        String sql = "DELETE FROM " + tableName;
        try (Statement stmt = c.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}