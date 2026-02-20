package org.WHITECN.rods;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.WHITECN.utils.AdvancementHandler;
import org.WHITECN.utils.ConfigManager;
import org.WHITECN.utils.SQLiteUtils;
import org.WHITECN.utils.tagUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;


public class RegularProRod extends AbstractRod{
    public RegularProRod() {
        super(
                Vars.PRO_ROD_NAME,
                "pro",
                80*20,
                Arrays.asList("§7普通末地烛的§bPro§7版")
        );
    }

    @Override
    public void onUse(Player player, Player target) {
        Plugin plug = JavaPlugin.getPlugin(anendrod.class);
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 50 * 20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80 * 20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80 * 20, 2));
        //持续80喵
        new BukkitRunnable() {
            private int t = 0; // 当前 tick喵

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || t > 80 * 20) {
                    cancel();
                    return;
                }
                /* -------- 0-30 秒：前奏喵 -------- */
                if (t <= 30 * 20) {
                    if (t % 40 == 0) {//2喵
                        player.damage(0.5d);
                        player.setNoDamageTicks(0);
                    }

                    // 水滴：每秒 6 颗
                    if (t % 20 == 0)
                        player.getWorld().spawnParticle(Particle.DRIP_WATER,
                                player.getLocation().add(0, -0.8, 0),
                                6, 0.2, -0.8, 0.2, 0.2);

                    // 爱心：每秒 3-4 颗
                    if (t % 20 == 0)
                        player.getWorld().spawnParticle(Particle.HEART,
                                player.getLocation().add(0, 1.6, 0),
                                random.nextInt(3) + 2, 0.2, 0.2, 0.2);

                    /* awa */
                    if (t == 0 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "唔...插进来了喵♥♥...");
                    } else if (t == 3 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "有点涨♥...");
                    } else if (t == 7 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "里面...在动♥...");
                    } else if (t == 12 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "不行...要化了♥♥...");
                    } else if (t == 18 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "再坚持一下♥...");
                    } else if (t == 25 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "快到极限了♥...");
                    }
                    if (t % 10 == 0)
                        player.playSound(player.getLocation(),
                                Insert_sounds.get(random.nextInt(Insert_sounds.size())),
                                1f, 1f);
                }

                /* -------- 30-60 秒：高速 -------- */
                else if (30 * 20 < t && t <= 60 * 20) {
                    if (t == 610) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 10));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 10));
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "唔....♥好了喵....♥");
                    }
                    // 水滴 4× 速：每 5 tick
                    if (t % 5 == 0)
                        player.getWorld().spawnParticle(Particle.DRIP_WATER,
                                player.getLocation().add(0, 0.8, 0),
                                8, 0.2, -0.8, 0.2, 0.3);

                    // 爱心 2× 速：每 10 tick
                    if (t % 10 == 0)
                        player.getWorld().spawnParticle(Particle.HEART,
                                player.getLocation().add(0, 1.6, 0),
                                random.nextInt(10) + 10, 0.2, 0, 0.2, 0);
                    if (t % 5 == 0)
                        player.playSound(player.getLocation(),
                                Insert_sounds.get(random.nextInt(Insert_sounds.size())),
                                1f, 1f);
                    if (t == 33 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "舒服了，但是唔。。。");
                    } else if (t == 42 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "哈....哈..♥");
                    } else if (t == 48 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "不行，不该这样的喵♥♥...");
                    } else if (t == 53 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "啊哈♥...");
                    } else if (t == 57 * 20) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "要去了喵，不要，哇啊...");
                    }
                    if (t == 1160) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "不能再这样下去了喵♥♥....唔...");
                    }
                } else {
                    if (t == 1210) {
                        if (player.getLocation().getBlock().getType() == Material.AIR || player.getLocation().getBlock().getType() == Material.CAVE_AIR || player.getLocation().getBlock().getType() == Material.VOID_AIR) {
                            player.sendMessage(ChatColor.GRAY + "哗啦");
                            if (ConfigManager.PRO_SPAWN_WATER && Objects.requireNonNull(player.getLocation().getWorld()).getEnvironment() != World.Environment.NETHER) {
                                player.getWorld().getBlockAt(player.getLocation()).setType(Material.WATER);
                            }
                        }
                    }

                    if (t % 2 == 0)
                        player.getWorld().spawnParticle(Particle.HEART,
                                player.getLocation().add(0, 1.6, 0),
                                7, 0.3, 0.3, 0.3, 0);
                }
                t++;
            }
        }.runTaskTimer(plug, 0L, 1L);
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack rod = createBaseItemStack();
        ItemMeta meta = rod.getItemMeta();
        meta = updateItemLore(meta);
        rod.setItemMeta(meta);
        return rod;
    }
    @Override
    public void addRecipeIngredients() {
        getRecipe().addIngredient(9, Material.END_ROD);
    }
}
