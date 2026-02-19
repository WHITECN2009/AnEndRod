package org.WHITECN.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class rodsHandler {
    private static Random random = new Random();
    public static List<Sound> Insert_sounds = Arrays.asList(
            Sound.ITEM_HONEYCOMB_WAX_ON,
            Sound.BLOCK_HONEY_BLOCK_HIT,
            Sound.BLOCK_HONEY_BLOCK_SLIDE,
            Sound.BLOCK_HONEY_BLOCK_STEP
    );
    public static void handleRegularRod(Player player, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1.0d);
        player.setCooldown(Material.END_ROD, 10);
        target.setNoDamageTicks(5);
        target.playSound(target, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        target.spawnParticle(Particle.HEART, target.getLocation(), 30, 1.5d, 1.0d, 1.5d);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));

        // 性能优化：使用 PDC 存储统计数据，减少数据库频繁 IO
        tagUtils.ensureTag(target, "rodUsed", "0");
        int rodUsed = Integer.parseInt(tagUtils.getTag(target, "rodUsed")) + 1;
        tagUtils.setTag(target, "rodUsed", String.valueOf(rodUsed));
        AdvancementHandler.advancementTest(target);

        // 异步更新数据库统计，避免阻塞主线程
        String playerName = player.getName();
        String targetName = target.getName();
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(anendrod.class), () -> {
            SQLiteUtils.setCTCount(playerName, SQLiteUtils.getCTCount(playerName) + 1);
            SQLiteUtils.setChaCount(targetName, SQLiteUtils.getChaCount(targetName) + 1);
        });
    }

    public static void handleSlimeRod(Player player, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1d);
        if (ConfigManager.SUMMON_SLIME) {
            for (int i = 0; i < Math.abs(random.nextInt(2)); i++) {
                Slime entity = (Slime) target.getWorld().spawnEntity(target.getLocation(), EntityType.SLIME);
                entity.setCustomNameVisible(true);
                entity.setCustomName(ChatColor.LIGHT_PURPLE + target.getName() + "的末地烛的" + ChatColor.GREEN + "附着物");
                entity.setSize(1);
            }
        }
        player.setCooldown(Material.END_ROD, 10);
        target.setNoDamageTicks(5);
        target.playSound(player, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        target.spawnParticle(Particle.HEART, player.getLocation(), 30, 1.5d, 1.0d, 1.5d);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d呜嗯...进去了~"));

        // 性能优化：使用 PDC 存储统计数据
        tagUtils.ensureTag(target, "rodUsed", "0");
        int rodUsed = Integer.parseInt(tagUtils.getTag(target, "rodUsed")) + 1;
        tagUtils.setTag(target, "rodUsed", String.valueOf(rodUsed));
        AdvancementHandler.advancementTest(target);

        // 异步更新数据库统计
        String playerName = player.getName();
        String targetName = target.getName();
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(anendrod.class), () -> {
            SQLiteUtils.setCTCount(playerName, SQLiteUtils.getCTCount(playerName) + 1);
            SQLiteUtils.setChaCount(targetName, SQLiteUtils.getChaCount(targetName) + 1);
        });
    }


    public static void handleRegularProRod(Player player) {
        Plugin plug = JavaPlugin.getPlugin(anendrod.class);
        player.setCooldown(Material.END_ROD, 80 * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 50 * 20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80 * 20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80 * 20, 2));

        SQLiteUtils.setChaCount(player.getName(), SQLiteUtils.getChaCount(player.getName()) + 1);
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
                            if (ConfigManager.PRO_SPAWN_WATER && Objects.requireNonNull(player.getLocation().getWorld()).getEnvironment() != Environment.NETHER) {
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
        tagUtils.ensureTag(player, "rodUsed", "0");
        tagUtils.setTag(player, "rodUsed", String.valueOf(Integer.parseInt(tagUtils.getTag(player, "rodUsed")) + 1));
        AdvancementHandler.advancementTest(player);
    }

    public static void handlePotionRod(Player player, Player target, ItemMeta meta) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        target.damage(1.0d);
        player.setCooldown(Material.END_ROD, 10);
        target.setNoDamageTicks(5);
        target.playSound(target, Insert_sounds.get(random.nextInt(Insert_sounds.size())), 1.0f, 1.0f);
        target.spawnParticle(Particle.HEART, target.getLocation(), 30, 1.5d, 1.0d, 1.5d);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d什么东西...?"));

        List<PotionEffect> effects = PotionUtils.parseString( meta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING)); //还可以更长呢！
        for (PotionEffect effect : effects) {
            target.addPotionEffect(effect);
        }

        // 性能优化：使用 PDC 存储统计数据，减少数据库频繁 IO
        tagUtils.ensureTag(target, "rodUsed", "0");
        int rodUsed = Integer.parseInt(tagUtils.getTag(target, "rodUsed")) + 1;
        tagUtils.setTag(target, "rodUsed", String.valueOf(rodUsed));
        AdvancementHandler.advancementTest(target);

        // 异步更新数据库统计，避免阻塞主线程
        String playerName = player.getName();
        String targetName = target.getName();
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(anendrod.class), () -> {
            SQLiteUtils.setCTCount(playerName, SQLiteUtils.getCTCount(playerName) + 1);
            SQLiteUtils.setChaCount(targetName, SQLiteUtils.getChaCount(targetName) + 1);
        });
    }
}
