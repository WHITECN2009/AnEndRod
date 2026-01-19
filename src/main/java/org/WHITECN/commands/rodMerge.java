package org.WHITECN.commands;

import org.WHITECN.anendrod;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.ItemGenerator;
import org.WHITECN.utils.tagUtils;
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

import static org.WHITECN.anendrod.prefix;

public class rodMerge implements CommandExecutor, Listener ,TabCompleter{
    private final JavaPlugin plugin;

    public rodMerge(JavaPlugin plugin) {
        this.plugin = plugin;
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "§c该命令仅能被玩家执行喵");
                return true;
            }
            Player player = (Player) sender;
            openGUI(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                if (!sender.isOp()) {
                    sender.sendMessage(prefix + "§c你没有权限使用 reload 喵~");
                    return true;
                }
                ConfigManager.loadConfig(anendrod.getInstance());
                sender.sendMessage(prefix + "§a配置已重载喵~");
                return true;

            case "togglecuff":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix + "§c该命令仅能被玩家执行喵");
                    return true;
                }
                Player player = (Player) sender;
                tagUtils.ensureTag(player, "canCuff", "false");
                if (tagUtils.getTag(player, "canCuff").equals("false")) {
                    tagUtils.setTag(player, "canCuff", "true");
                    player.sendMessage(prefix + "§a已开启手铐玩法喵！");
                } else {
                    tagUtils.setTag(player, "canCuff", "false");
                    player.sendMessage(prefix + "§c已关闭手铐玩法喵！");
                }
                return true;

            case "setrodused":
                if (!sender.isOp()) {
                    sender.sendMessage(prefix + "§c你没有权限使用 setrodused 喵~");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(prefix + "§c用法: /rodmerge setrodused <玩家> <次数>");
                    return true;
                }
                try {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(prefix + "§c未查找到该玩家喵");
                        return true;
                    }
                    int usedCount = Integer.parseInt(args[2]);
                    tagUtils.ensureTag(target, "rodUsed", "0");
                    tagUtils.setTag(target, "rodUsed", String.valueOf(usedCount));
                    sender.sendMessage(prefix + "§a已将玩家 " + target.getName() + " 的使用次数设置为 " + usedCount + " 喵~");
                } catch (NumberFormatException e) {
                    sender.sendMessage(prefix + "§c请输入有效的整数喵!");
                }
                return true;

            case "getrodused":
                if (!sender.isOp()) {
                    sender.sendMessage(prefix + "§c你没有权限使用 getrodused 喵~");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§c用法: /rodmerge getrodused <玩家>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(prefix + "§c未查找到该玩家喵");
                    return true;
                }
                tagUtils.ensureTag(target, "rodUsed", "0");
                sender.sendMessage(prefix + "§b该玩家 " + target.getName() + " 的末地烛使用次数: §a" + tagUtils.getTag(target, "rodUsed"));
                return true;

            default:
                sender.sendMessage(prefix + "§c未知子命令喵。使用 /rodmerge gui 打开菜单。");
                return true;
        }
    }

    private void openGUI(Player player) {
        Inventory mergeUI = Bukkit.createInventory(player, 18, "§9§l兑换小玩具");

        //TODO:此处注册新的物品
        ItemStack regularRod = createMenuItem(Material.END_ROD, "§2普通末地烛", "§7没什么特别的 就是末地烛哦");
        ItemStack slimeRod = createMenuItem(Material.END_ROD, "§a粘液§2末地烛", "§7一个黏糊糊的末地烛哦");
        ItemStack proRod = createMenuItem(Material.END_ROD, "§bPro§2末地烛", "§7普通末地烛的§bPro§7版");
        ItemStack handCuff = createMenuItem(Material.CHAINMAIL_CHESTPLATE, "§d手铐♥", "§d这是一个手铐，可以限制玩家的行动");
        ItemStack keyItem = createMenuItem(Material.TRIPWIRE_HOOK, "§7钥匙", "§d这是一个钥匙，可以解锁也可以上锁");

        //TODO:此处加载进菜单
        mergeUI.addItem(regularRod);
        mergeUI.addItem(slimeRod);
        mergeUI.addItem(proRod);
        mergeUI.setItem(9, handCuff);
        mergeUI.setItem(10, keyItem);

        player.openInventory(mergeUI);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (!sender.isOp()) {
            list.add("togglecuff");
            list.add("gui");
            return list;
        }
        if (args.length == 1) {
            list.add("reload");
            list.add("setrodused");
            list.add("getrodused");
            list.add("togglecuff");
            list.add("gui");
            return list;
        }else if (args.length == 2 && (args[0].equalsIgnoreCase("setrodused") || args[0].equalsIgnoreCase("getrodused"))) {
            List<String> playerNames = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                playerNames.add(online.getName());
            }
            return playerNames;
        }else if (args.length == 3 && args[0].equalsIgnoreCase("setrodused")) {
            list.add("请输入要修改到的使用次数(仅限整数)");
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
        if (inventory.getHolder() instanceof Player && event.getView().getTitle().equals("§9§l兑换小玩具")) {

            event.setCancelled(true); // 防止移动物品

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String itemName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();

            Inventory inv = player.getInventory();

            // 根据点击的物品执行不同操作
            switch (itemName) {
                case "§2普通末地烛":
                    ItemStack regularRod = ItemGenerator.createRegularRod();
                    if (regularCheck(inv)) {
                        inv.addItem(regularRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 普通末地烛 喵, 需要:末地烛x1");
                    break;
                case "§a粘液§2末地烛":
                    ItemStack slimeRod = ItemGenerator.createSlimeRod();
                    if (slimeCheck(inv)) {
                        inv.addItem(slimeRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 粘液末地烛 喵, 需要:末地烛x1 粘液球x1");
                    break;
                case "§bPro§2末地烛":
                    ItemStack proRod = ItemGenerator.createRegularProRod();
                    if (proCheck(inv)) {
                        inv.addItem(proRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 Pro末地烛 喵, 需要:末地烛x9");
                    break;
                case "§d手铐♥":
                    ItemStack handCuff = ItemGenerator.createHandCuffs();
                    if (handcuffCheck(inv)) {
                        inv.addItem(handCuff);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 手铐 喵, 需要:铁锭x2 链条x2");
                    break;
                case "§7钥匙":
                    ItemStack key = ItemGenerator.createKeyItem();
                    if (keyCheck(inv)) {
                        inv.addItem(key);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 钥匙 喵, 需要:铁锭x1 木棍x1");
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

    /**
     * 手铐兑换检查（需要：铁锭 x2，链条 x2）
     * 会在满足条件时从背包中扣除对应物品（支持分散在多个槽的堆叠）
     */
    private Boolean handcuffCheck(Inventory inv) {
        int needIron = 2;
        int needChain = 2;

        // 先统计是否满足条件
        int foundIron = 0;
        int foundChain = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.IRON_INGOT) foundIron += item.getAmount();
            if (item.getType() == Material.CHAIN) foundChain += item.getAmount();
            if (foundIron >= needIron && foundChain >= needChain) break;
        }

        if (foundIron < needIron || foundChain < needChain) return false;

        // 扣除铁锭
        int toRemove = needIron;
        for (int i = 0; i < inv.getSize() && toRemove > 0; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getType() != Material.IRON_INGOT) continue;
            if (item.getAmount() > toRemove) {
                item.setAmount(item.getAmount() - toRemove);
                toRemove = 0;
            } else {
                toRemove -= item.getAmount();
                inv.setItem(i, null);
            }
        }

        // 扣除链条
        toRemove = needChain;
        for (int i = 0; i < inv.getSize() && toRemove > 0; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getType() != Material.CHAIN) continue;
            if (item.getAmount() > toRemove) {
                item.setAmount(item.getAmount() - toRemove);
                toRemove = 0;
            } else {
                toRemove -= item.getAmount();
                inv.setItem(i, null);
            }
        }

        return true;
    }

    /**
     * 钥匙兑换检查（需要：铁锭 x1，木棍 x1）
     * 会在满足条件时从背包中扣除对应物品（支持分散在多个槽的堆叠）
     */
    private Boolean keyCheck(Inventory inv) {
        int needIron = 1;
        int needStick = 1;

        int foundIron = 0;
        int foundStick = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.IRON_INGOT) foundIron += item.getAmount();
            if (item.getType() == Material.STICK) foundStick += item.getAmount();
            if (foundIron >= needIron && foundStick >= needStick) break;
        }

        if (foundIron < needIron || foundStick < needStick) return false;

        // 扣除铁锭
        int toRemove = needIron;
        for (int i = 0; i < inv.getSize() && toRemove > 0; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getType() != Material.IRON_INGOT) continue;
            if (item.getAmount() > toRemove) {
                item.setAmount(item.getAmount() - toRemove);
                toRemove = 0;
            } else {
                toRemove -= item.getAmount();
                inv.setItem(i, null);
            }
        }

        // 扣除木棍
        toRemove = needStick;
        for (int i = 0; i < inv.getSize() && toRemove > 0; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getType() != Material.STICK) continue;
            if (item.getAmount() > toRemove) {
                item.setAmount(item.getAmount() - toRemove);
                toRemove = 0;
            } else {
                toRemove -= item.getAmount();
                inv.setItem(i, null);
            }
        }

        return true;
    }
}
