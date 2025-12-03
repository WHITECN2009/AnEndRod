package org.WHITECN.commands;

import org.WHITECN.anendrod;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.rodItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class rodMerge implements CommandExecutor, Listener ,TabCompleter{
    String prefix = "§9[EndRod]§r ";

    private final JavaPlugin plugin;

    public rodMerge(JavaPlugin plugin) {
        this.plugin = plugin;
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage(prefix + "§c你没有权限使用 reload 喵~");
                return true;
            }
            ConfigManager.loadConfig(anendrod.getInstance());
            sender.sendMessage(prefix + "§a配置已重载喵~");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§c该命令仅能被玩家执行喵");
            return true;
        }
        Player player = (Player) sender;
        
        if (args.length != 0) {
            sender.sendMessage(prefix + "§c用法:/rodmerge");
            return true;
        }
        Inventory mergeUI = Bukkit.createInventory(player,9,"§9§l兑换末地烛");

        //TODO:此处注册新的末地烛
        ItemStack regularRod = createMenuItem(Material.END_ROD,"§2普通末地烛","§7没什么特别的 就是末地烛哦");
        ItemStack slimeRod = createMenuItem(Material.END_ROD,"§a粘液§2末地烛","§7一个黏糊糊的末地烛哦");
        ItemStack proRod = createMenuItem(Material.END_ROD,"§bPro§2末地烛","§7普通末地烛的§bPro§7版");

        //TODO:此处加载进菜单
        mergeUI.addItem(regularRod);
        mergeUI.addItem(slimeRod);
        mergeUI.addItem(proRod);

        player.openInventory(mergeUI);
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1 && sender.isOp()) {
            List<String> list = new ArrayList<>();
            list.add("reload");
            return list;
        }
        return Collections.emptyList(); // 其余情况什么都没有！
    }
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        // 检查是否点击了我们的菜单
        if (inventory.getHolder() instanceof Player && event.getView().getTitle().equals("§9§l兑换末地烛")) {

            event.setCancelled(true); // 防止移动物品

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String itemName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();

            Inventory inv = player.getInventory();

            // 根据点击的物品执行不同操作
            switch (itemName) {
                case "§2普通末地烛":
                    ItemStack regularRod = rodItemGenerator.createRegularRod();
                    if (regularCheck(inv)) {
                        inv.addItem(regularRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 普通末地烛 喵, 需要:末地烛x1");
                    break;
                case "§a粘液§2末地烛":
                    ItemStack slimeRod = rodItemGenerator.createSlimeRod();
                    if (slimeCheck(inv)) {
                        inv.addItem(slimeRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 粘液末地烛 喵, 需要:末地烛x1 粘液球x1");
                    break;
                case "§bPro§2末地烛":
                    ItemStack proRod = rodItemGenerator.createRegularProRod();
                    if (proCheck(inv)) {
                        inv.addItem(proRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 粘液末地烛 喵, 需要:末地烛x9");
                    break;
            }
        }
    }

    private static ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(line);
        }
        meta.setLore(loreList);

        item.setItemMeta(meta);
        return item;
    }

    private Boolean regularCheck(Inventory inv){
        for (ItemStack item : inv.getContents()){
            if (item != null && item.getType().equals(Material.END_ROD) && !Objects.requireNonNull(item.getItemMeta()).hasLore()){
                item.setAmount(item.getAmount() - 1);
                return true;
            }
        }
        return false;
    }
    private Boolean proCheck(Inventory inv){
        for (ItemStack item : inv.getContents()){
            if (item != null && item.getType().equals(Material.END_ROD) && !Objects.requireNonNull(item.getItemMeta()).hasLore() && item.getAmount() >= 9){
                item.setAmount(item.getAmount() - 9);
                return true;
            }
        }
        return false;
    }

    private Boolean slimeCheck(Inventory inv){
        boolean hasEndRod = false;
        boolean hasSlimeBall = false;
        int endRodSlot = -1;
        int slimeBallSlot = -1;

        // 先检测是否同时拥有两个物品
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) {
                // 检测末地烛（没有lore的普通末地烛）
                if (!hasEndRod && item.getType().equals(Material.END_ROD) &&
                        !Objects.requireNonNull(item.getItemMeta()).hasLore()) {
                    hasEndRod = true;
                    endRodSlot = i;
                }
                // 检测粘液球
                if (!hasSlimeBall && item.getType().equals(Material.SLIME_BALL)) {
                    hasSlimeBall = true;
                    slimeBallSlot = i;
                }
            }
        }

        // 如果两个物品都有，则删除它们
        if (hasEndRod && hasSlimeBall) {
            // 删除末地烛
            ItemStack endRod = inv.getItem(endRodSlot);
            if (endRod.getAmount() > 1) {
                endRod.setAmount(endRod.getAmount() - 1);
            } else {
                inv.setItem(endRodSlot, null);
            }

            // 删除粘液球
            ItemStack slimeBall = inv.getItem(slimeBallSlot);
            if (slimeBall.getAmount() > 1) {
                slimeBall.setAmount(slimeBall.getAmount() - 1);
            } else {
                inv.setItem(slimeBallSlot, null);
            }

            return true;
        }

        return false;
    }
}
