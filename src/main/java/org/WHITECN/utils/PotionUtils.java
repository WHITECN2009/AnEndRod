package org.WHITECN.utils;

import org.WHITECN.Vars;
import org.WHITECN.anendrod;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionUtils {
    private static final Integer REDUCE_SECOND = 5;

    public static List<PotionEffect> parseString(String nya){
        if (nya == null) return new ArrayList<>();
        String[] result = nya.split("[{}]+");
        List<PotionEffect> effects = new ArrayList<>();
        for (int i = 1; i < result.length; i++) {
            String nyan = result[i];
            int duration =-1,amplifier = -1;
            PotionEffectType pet = null;

            String tmp = "";
            int tmp2 = 0;
            for (int j = 0;j< nyan.length();j++){
                char c = nyan.charAt(j);
                if (c == ':'){
                    pet = PotionEffectType.getByName(tmp);
                    tmp = "";
                } else if ((c == 't' || c == '-' || c == 'x') && duration == -1) {
                    duration = tmp2;
                    tmp2 = 0;
                }else if (!(c == 't' || c == '-' || c == 'x')){
                    tmp += c;
                    if (isDigit(c)){
                        tmp2 *=10;
                        tmp2 += c - '0';
                    }
                }
            }
            amplifier = tmp2;

            effects.add(new PotionEffect(pet,duration,amplifier));
        }
        return effects;
    }

    private static Boolean isDigit(Character c){
        return ('0' <= c && c <= '9');
    }

    public static String getString(List<PotionEffect> effects) {
        //return type.getName() + (ambient ? ":(" : ":") + duration + "t-x" + amplifier + (ambient ? ")" : "");
        StringBuilder sb = new StringBuilder();
        for (PotionEffect pe : effects) sb.append('{' + pe.getType().getName() +
                ":" + pe.getDuration() +
                "t-x" + pe.getAmplifier() + '}');
        return sb.toString();
    }

    private static String reduceDur(String nya){
        List<PotionEffect> originalEffects = parseString(nya);
        List<PotionEffect> reducedEffects = new ArrayList<>();

        for (PotionEffect effect : originalEffects) {
            int reducedDuration = effect.getDuration() - REDUCE_SECOND * 20;
            if (reducedDuration < 0) continue;

            reducedEffects.add(new PotionEffect(
                    effect.getType(),
                    reducedDuration,
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            ));
        }
        return getString(reducedEffects);
    }
    public static Boolean hasPotion(ItemMeta itemMeta) {
        if (itemMeta == null) return false;
        return itemMeta.getPersistentDataContainer().has(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING) &&
                !parseString(itemMeta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING)).isEmpty();
    }
    public static String getPotion(ItemMeta itemMeta) {
        if (itemMeta == null) return null;
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(anendrod.getInstance(), Vars.NAMESPACE_POTION), PersistentDataType.STRING);
    }
    public static List<String> toStringList(List<PotionEffect> effects){
        List<String> result = new ArrayList<>();
        for (PotionEffect pe : effects){
            result.add((isPositive(pe.getType()) ? "§a" : "§c") + getChineseName(pe.getType()) +toRomanOptimized(pe.getAmplifier()) + " " +pe.getDuration()/20 + "秒");
        }
        return result;
    }
    private static String toRomanOptimized(int number) {
        if (number == 0) return "N";
        String[] hundreds = {"", "C", "CC"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        StringBuilder roman = new StringBuilder();
        roman.append(hundreds[number / 100]);
        number %= 100;
        roman.append(tens[number / 10]);
        roman.append(ones[number % 10]);
        return roman.toString();
    }
    public static ItemMeta setPotion(ItemMeta meta) { //减少
        if (meta == null) return null;
        anendrod plugin = anendrod.getInstance();
        NamespacedKey potionsKey = new NamespacedKey(plugin, Vars.NAMESPACE_POTION);
        if (meta.getPersistentDataContainer().has(potionsKey, PersistentDataType.STRING)) {
            String nya = meta.getPersistentDataContainer().get(potionsKey, PersistentDataType.STRING);
            meta.getPersistentDataContainer().set(potionsKey, PersistentDataType.STRING, reduceDur(nya));
            return meta;
        }
        return meta;
    }
    public static String getChineseName(PotionEffectType type) {
        if (type == PotionEffectType.SPEED) return "速度";
        if (type == PotionEffectType.SLOW) return "缓慢";
        if (type == PotionEffectType.FAST_DIGGING) return "急迫";
        if (type == PotionEffectType.SLOW_DIGGING) return "挖掘疲劳";
        if (type == PotionEffectType.INCREASE_DAMAGE) return "力量";
        if (type == PotionEffectType.HEAL) return "瞬间治疗";
        if (type == PotionEffectType.HARM) return "瞬间伤害";
        if (type == PotionEffectType.JUMP) return "跳跃提升";
        if (type == PotionEffectType.CONFUSION) return "反胃";
        if (type == PotionEffectType.REGENERATION) return "生命恢复";
        if (type == PotionEffectType.DAMAGE_RESISTANCE) return "抗性提升";
        if (type == PotionEffectType.FIRE_RESISTANCE) return "防火";
        if (type == PotionEffectType.WATER_BREATHING) return "水下呼吸";
        if (type == PotionEffectType.INVISIBILITY) return "隐身";
        if (type == PotionEffectType.BLINDNESS) return "失明";
        if (type == PotionEffectType.NIGHT_VISION) return "夜视";
        if (type == PotionEffectType.HUNGER) return "饥饿";
        if (type == PotionEffectType.WEAKNESS) return "虚弱";
        if (type == PotionEffectType.POISON) return "中毒";
        if (type == PotionEffectType.WITHER) return "凋零";
        if (type == PotionEffectType.HEALTH_BOOST) return "生命提升";
        if (type == PotionEffectType.ABSORPTION) return "伤害吸收";
        if (type == PotionEffectType.SATURATION) return "饱和";
        if (type == PotionEffectType.GLOWING) return "发光";
        if (type == PotionEffectType.LEVITATION) return "飘浮";
        if (type == PotionEffectType.LUCK) return "幸运";
        if (type == PotionEffectType.UNLUCK) return "霉运";
        if (type == PotionEffectType.SLOW_FALLING) return "缓降";
        if (type == PotionEffectType.CONDUIT_POWER) return "潮涌能量";
        if (type == PotionEffectType.DOLPHINS_GRACE) return "海豚的恩惠";
        if (type == PotionEffectType.BAD_OMEN) return "不祥之兆";
        if (type == PotionEffectType.HERO_OF_THE_VILLAGE) return "村庄英雄";
        if (type == PotionEffectType.DARKNESS) return "黑暗";
        return type.getName();
    }

    public static boolean isPositive(PotionEffectType type) {
        if (type == PotionEffectType.BAD_OMEN) return false;
        if (type == PotionEffectType.BLINDNESS) return false;
        if (type == PotionEffectType.DARKNESS) return false;
        if (type == PotionEffectType.HUNGER) return false;
        if (type == PotionEffectType.HARM) return false;
        if (type == PotionEffectType.SLOW_DIGGING) return false;
        if (type == PotionEffectType.CONFUSION) return false;
        if (type == PotionEffectType.POISON) return false;
        if (type == PotionEffectType.SLOW) return false;
        if (type == PotionEffectType.UNLUCK) return false;
        if (type == PotionEffectType.WEAKNESS) return false;
        if (type == PotionEffectType.WITHER) return false;
        if (type == PotionEffectType.LEVITATION) return false;
        return true;
    }
}
