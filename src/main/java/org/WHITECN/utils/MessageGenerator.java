package org.WHITECN.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MessageGenerator {

    // 定义时直接写死内容
    private static final List<String> MESSAGES = Arrays.asList(
        "唔...♥",
        "哈..唔♥",
        "唔♥",
        "喵♥",
        "喵！"
    );

    private static final Random RANDOM = new Random();
    public static String getRMessage() {
        return MESSAGES.get(RANDOM.nextInt(MESSAGES.size()));
    }
}