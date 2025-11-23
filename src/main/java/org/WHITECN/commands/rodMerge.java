package org.WHITECN.commands;

import org.WHITECN.utils.rodItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class rodMerge implements CommandExecutor, Listener {
    String prefix = "§9[EndRod]§r ";

    private final JavaPlugin plugin;

    public rodMerge(JavaPlugin plugin) {
        this.plugin = plugin;
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

        //TODO:此处加载进菜单
        mergeUI.addItem(regularRod);
        mergeUI.addItem(slimeRod);

        player.openInventory(mergeUI);
        return true;
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

            // 根据点击的物品执行不同操作
            Inventory inv = player.getInventory();
            switch (itemName) {
                case "§2普通末地烛":
                    ItemStack regularRod = rodItemGenerator.createRegularRod();
                    if (hasRawRod(inv)) {
                        inv.addItem(regularRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 普通末地烛 喵, 需要:末地烛x1");
                    break;
                case "§a粘液§2末地烛":
                    ItemStack slimeRod = rodItemGenerator.createSlimeRod();
                    if (hasRawRod(inv)) {
                        inv.addItem(slimeRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 粘液末地烛 喵, 需要:末地烛x1");
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

    private Boolean hasRawRod(Inventory inv){
        for (ItemStack item : inv.getContents()){
            if (item != null && item.getType().equals(Material.END_ROD) && !Objects.requireNonNull(item.getItemMeta()).hasLore()){
                item.setAmount(item.getAmount() - 1);
                return true;
            }
        }
        return false;
    }
}
