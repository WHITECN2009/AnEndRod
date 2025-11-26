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
        "喵！",
        "哈啊...喵呜♥",
        "喵喵..喘唔♥",
        "唔...喵♥",
        "喘啊..喵喵♥",
        "喵呜！哈啊♥",
        "嗯唔...喵♥",
        "喵..哈啊♥",
        "喘唔喵呜♥",
        "喵喵..嗯啊♥",
        "喵！喘唔♥",
        "哈啊喵...♥",
        "喘唔..喵♥",
        "喵呜嗯啊♥",
        "喵喵..喘♥",
        "喵！哈啊♥"
    );

    private static final Random RANDOM = new Random();
    public static String getRMessage() {
        return MESSAGES.get(RANDOM.nextInt(MESSAGES.size()));
    }
}