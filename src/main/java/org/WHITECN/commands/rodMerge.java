package org.WHITECN.commands;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.rods.PotionRod;
import org.WHITECN.rods.RegularProRod;
import org.WHITECN.rods.RegularRod;
import org.WHITECN.rods.SlimeRod;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.ItemGenerator;
import org.WHITECN.utils.tagUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

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

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "setrodused":
                    if (!sender.isOp()) {
                        sender.sendMessage(prefix + "§c你没有权限使用 setrodused 喵~");
                        return true;
                    }
                    try{
                        if (args.length != 3){
                            sender.sendMessage(prefix + "杂鱼...");
                            sender.sendMessage(prefix + "长这么大了还不知道格式是什么嘛...");
                            sender.sendMessage(prefix + "是/rodmerge setrodused <player> <count>");
                            sender.sendMessage(prefix + "笨蛋..");
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        int usedCount = Integer.parseInt(args[2]);
                        if (target == null){
                            player.sendMessage(prefix + "§c未查找到该玩家喵");
                            return true;
                        }
                        tagUtils.ensureTag(target,"rodUsed","0");
                        tagUtils.setTag(target,"rodUsed",String.valueOf(usedCount));
                    }catch (ClassCastException e){
                        player.sendMessage(prefix + "§c请按照提示要求输入喵!");
                        return true;
                    }
                    return true;

                case "getrodused":
                    if (!sender.isOp()) {
                        sender.sendMessage(prefix + "§c你没有权限使用 setrodused 喵~");
                        return true;
                    }
                    if (args.length != 2){
                        sender.sendMessage(prefix + "笨蛋笨蛋！你要查询谁啊！");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(prefix + "§c未查找到该玩家喵");
                        return true;
                    }
                    tagUtils.ensureTag(target, "rodUsed", "0");
                    sender.sendMessage(prefix + "§b该玩家的末地烛使用次数: §a" + tagUtils.getTag(target, "rodUsed"));
                    return true;

                case "togglecuff":
                    tagUtils.ensureTag(player, "canCuff", "false");
                    if (tagUtils.getTag(player,"canCuff").equals("false")){
                        tagUtils.setTag(player, "canCuff", "true");
                        player.sendMessage(prefix + "§a已开启手铐玩法喵！");
                    }else{
                        tagUtils.setTag(player, "canCuff", "false");
                        player.sendMessage(prefix + "§c已关闭手铐玩法喵！");
                    }
                    return true;
            }
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            Inventory mergeUI = Bukkit.createInventory(player, 18, "§9§l兑换小玩具");

            //TODO:此处注册新的物品
            ItemStack regularRod = new RegularRod().createBaseItemStack();
            ItemStack slimeRod = createMenuItem(Material.END_ROD, Vars.SLIME_ROD_NAME, "§7一个黏糊糊的末地烛哦");
            ItemStack proRod = createMenuItem(Material.END_ROD, Vars.PRO_ROD_NAME, "§7普通末地烛的§bPro§7版");
            ItemStack potionRod = createMenuItem(Material.END_ROD, Vars.POTION_ROD_NAME, "§7可以沾药水的末地烛哦");
            ItemStack handCuff = createMenuItem(Material.CHAINMAIL_CHESTPLATE, "§d手铐♥", "§d这是一个手铐，可以限制玩家的行动");
            ItemStack keyItem = createMenuItem(Material.TRIPWIRE_HOOK, "§7钥匙", "§d这是一个钥匙，可以解锁也可以上锁");

            //TODO:此处加载进菜单
            mergeUI.addItem(regularRod);
            mergeUI.addItem(slimeRod);
            mergeUI.addItem(proRod);
            mergeUI.addItem(potionRod);
            mergeUI.setItem(9, handCuff);
            mergeUI.setItem(10, keyItem);

            player.openInventory(mergeUI);
            return true;
        }
        return true;
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
                case Vars.REGULAR_ROD_NAME:
                    RegularRod regularRodObj = new RegularRod();
                    ItemStack is = regularRodObj.createItemStack();
                    if (checkInv(regularRodObj.getRecipe(),inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        removeFromInv(regularRodObj.getRecipe(),inv);
                        inv.addItem(is);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 普通末地烛 喵, 需要:末地烛x1");
                    break;
                case Vars.SLIME_ROD_NAME:
                    SlimeRod slimeRodObj = new SlimeRod();
                    ItemStack slimeRod = slimeRodObj.createItemStack();
                    if (checkInv(slimeRodObj.getRecipe(),inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        removeFromInv(slimeRodObj.getRecipe(),inv);
                        inv.addItem(slimeRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 粘液末地烛 喵, 需要:末地烛x1 粘液球x1");
                    break;
                case Vars.PRO_ROD_NAME:
                    RegularProRod proRodObj = new RegularProRod();
                    ItemStack proRod = proRodObj.createItemStack();
                    if (checkInv(proRodObj.getRecipe(),inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        removeFromInv(proRodObj.getRecipe(),inv);
                        inv.addItem(proRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 Pro末地烛 喵, 需要:末地烛x9");
                    break;
                case Vars.POTION_ROD_NAME:
                    PotionRod potionRodObj = new PotionRod();
                    ItemStack potionRod = potionRodObj.createItemStack();
                    if (checkInv(potionRodObj.getRecipe(),inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        removeFromInv(potionRodObj.getRecipe(),inv);
                        inv.addItem(potionRod);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 药水末地烛 喵, 需要:末地烛x1 玻璃瓶x1");
                    break;
                case "§d手铐♥":
                    ItemStack handCuff = ItemGenerator.createHandCuffs();
                    if (handcuffCheck(inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        inv.addItem(handCuff);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 手铐 喵, 需要:铁锭x2 链条x2");
                    break;
                case "§7钥匙":
                    ItemStack key = ItemGenerator.createKeyItem();
                    if (keyCheck(inv) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        inv.addItem(key);
                        player.sendMessage(prefix + "§2兑换成功喵~");
                        break;
                    }
                    player.sendMessage(prefix + "§c材料不足以兑换 钥匙 喵, 需要:铁锭x1 木棍x1");
                    break;
            }
        }
    }
    private boolean checkInv(ShapelessRecipe recipe, Inventory inv) {
        Map<ItemStack, Integer> required = new HashMap<>();
        for (ItemStack ingredient : recipe.getIngredientList()) {
            ItemStack key = ingredient.clone();
            key.setAmount(1); //防止比较不了的情况
            required.put(key, required.getOrDefault(key, 0) + ingredient.getAmount());
        }
        for (ItemStack invItem : inv.getContents()) {
            if (invItem == null || invItem.getType() == Material.AIR) continue;

            ItemStack matchingKey = null;
            for (ItemStack requiredItem : required.keySet()) {
                if (requiredItem.isSimilar(invItem)) {
                    matchingKey = requiredItem;
                    break;
                }
            }

            if (matchingKey != null) {
                int needed = required.get(matchingKey);
                int available = invItem.getAmount();

                if (available >= needed) {
                    required.remove(matchingKey);
                } else {
                    required.put(matchingKey, needed - available);
                }
            }
            if (required.isEmpty()) break; //剪枝（
        }
        return required.isEmpty();
    }
    private boolean removeFromInv(ShapelessRecipe recipe, Inventory inv) {
        Map<ItemStack, Integer> required = new HashMap<>();
        for (ItemStack ingredient : recipe.getIngredientList()) {
            ItemStack key = ingredient.clone();
            key.setAmount(1); //防止比较不了的情况
            required.put(key, required.getOrDefault(key, 0) + ingredient.getAmount());
        }
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack invItem = inv.getItem(i);
            if (invItem == null || invItem.getType() == Material.AIR) continue;
            ItemStack matchingKey = null;
            for (ItemStack requiredItem : required.keySet()) {
                if (requiredItem.isSimilar(invItem)) {
                    matchingKey = requiredItem;
                    break;
                }
            }

            if (matchingKey != null) {
                int needed = required.get(matchingKey);
                int available = invItem.getAmount();

                if (available > needed) {
                    invItem.setAmount(available - needed);
                    required.remove(matchingKey);
                } else if (available == needed) {
                    inv.setItem(i, null);
                    required.remove(matchingKey);
                } else {
                    inv.setItem(i, null);
                    required.put(matchingKey, needed - available);
                }
            }
            if (required.isEmpty()) break; //剪枝（
        }

        return required.isEmpty();
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

    /**
     * 药水末地烛兑换检查（需要：末地烛 x1，玻璃瓶 x1）
     * 会在满足条件时从背包中扣除对应物品（支持分散在多个槽的堆叠）
     */
    private Boolean potionCheck(Inventory inv) {
        boolean hasEndRod = false;
        boolean hasGlassBottle = false;
        int endRodSlot = -1;
        int glassBottleSlot = -1;

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
                // 检测玻璃瓶
                if (!hasGlassBottle && item.getType().equals(Material.GLASS_BOTTLE)) {
                    hasGlassBottle = true;
                    glassBottleSlot = i;
                }
            }
        }

        // 如果两个物品都有，则删除它们
        if (hasEndRod && hasGlassBottle) {
            // 删除末地烛
            ItemStack endRod = inv.getItem(endRodSlot);
            if (endRod.getAmount() > 1) {
                endRod.setAmount(endRod.getAmount() - 1);
            } else {
                inv.setItem(endRodSlot, null);
            }

            // 删除玻璃瓶
            ItemStack glassBottle = inv.getItem(glassBottleSlot);
            if (glassBottle.getAmount() > 1) {
                glassBottle.setAmount(glassBottle.getAmount() - 1);
            } else {
                inv.setItem(glassBottleSlot, null);
            }

            return true;
        }

        return false;
    }
}
