package com.zcunsoft.cfg;

import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * yauaa 8.3.0 能力探索:验证 UA 可提取的字段全集与 AI 机器人识别。
 * 全字段矩阵测试会把每类 UA 的解析结果打印到 surefire 输出,便于评估哪些字段值得入库。
 *
 * 实测结论(2026-11,yauaa 8.2.0/8.3.0 一致):
 * - 主流 AI 爬虫均识别为 AGENT_CLASS=Robot,并带 AGENT_INFORMATION_URL/EMAIL 自报联系方式;
 * - Claude-User(用户代访问)被 yauaa 归为 Browser,但 DEVICE_CLASS=Robot 且 AGENT_NAME 可识别;
 * - 因此识别机器人应使用:AGENT_CLASS=Robot 或 DEVICE_CLASS=Robot,再结合 AGENT_NAME 精确分类。
 */
class YauaaCapabilityTest {

    /** yauaa 对"无法解析"的哨兵值 */
    private static final Set<String> NOISE = Set.of("Unknown", "??", "<<UUNKNOWN>>", "");

    /** AI 爬虫/机器人 UA 矩阵:label -> (ua, 期望 AGENT_CLASS, 期望 AGENT_NAME) */
    private static final Map<String, BotCase> BOT_UAS = new LinkedHashMap<>() {
        {
            put("GPTBot(OpenAI 训练采集)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; GPTBot/1.1; +https://openai.com/gptbot", "Robot", "GPTBot"));
            put("OAI-SearchBot(OpenAI 搜索)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; OAI-SearchBot/1.1; +https://openai.com/searchbot", "Robot", "OAI-SearchBot"));
            put("ChatGPT-User(OpenAI 用户代访问)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; ChatGPT-User/1.0; +https://openai.com/bot", "Robot", "ChatGPT-User"));
            put("ClaudeBot(Anthropic 训练采集)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; ClaudeBot/1.0; +claudebot@anthropic.com", "Robot", "ClaudeBot"));
            // yauaa 把 Claude-User 归为 Browser,但 DEVICE_CLASS=Robot、AGENT_NAME=Claude-User 仍可识别
            put("Claude-User(Anthropic 用户代访问)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; Claude-User/1.0; +Claude-User@anthropic.com", "Browser", "Claude-User"));
            put("PerplexityBot(AI 搜索引擎)", new BotCase("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; PerplexityBot/1.0; +https://perplexity.ai/perplexitybot)", "Robot", "PerplexityBot"));
            put("meta-externalagent(Meta AI 训练采集)", new BotCase("meta-externalagent/1.1 (+https://developers.facebook.com/docs/sharing/webmasters/crawler)", "Robot", "Meta-ExternalAgent"));
            put("Bytespider(字节跳动)", new BotCase("Mozilla/5.0 (compatible; Bytespider; https://zhanzhang.toutiao.com/)", "Robot", "Bytespider"));
            put("Amazonbot(Amazon)", new BotCase("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.2.5 (KHTML, like Gecko) Amazonbot/0.1; +https://developer.amazon.com/support/amazonbot", "Robot", "Amazonbot"));
            put("Googlebot(对照:传统搜索爬虫)", new BotCase("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)", "Robot", "Googlebot"));
        }
    };

    /** 人类流量对照:label -> (ua, 期望 AGENT_CLASS, 期望 DEVICE_CLASS) */
    private static final Map<String, HumanCase> HUMAN_UAS = new LinkedHashMap<>() {
        {
            put("Chrome 桌面", new HumanCase("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36", "Browser", "Desktop"));
            put("iPhone Safari", new HumanCase("Mozilla/5.0 (iPhone; CPU iPhone OS 17_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Mobile/15E148 Safari/604.1", "Browser", "Phone"));
            put("微信内置浏览器", new HumanCase("Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.49(0x18003136) NetType/WIFI Language/zh_CN", "Mobile App", "Phone"));
        }
    };

    private record BotCase(String ua, String expectedAgentClass, String expectedAgentName) {
    }

    private record HumanCase(String ua, String expectedAgentClass, String expectedDeviceClass) {
    }

    private static UserAgentAnalyzer allFieldsAnalyzer() {
        return UserAgentAnalyzer.newBuilder().withAllFields().hideMatcherLoadStats().build();
    }

    /**
     * 打印全字段能力矩阵(61 个字段中每类 UA 实际能解析出的值),
     * 并断言机器人/人类识别与精确名称。
     */
    @Test
    void printsFullFieldMatrixForBotsAndHumans() {
        UserAgentAnalyzer analyzer = allFieldsAnalyzer();
        System.out.println("== yauaa " + analyzer.getClass().getPackage().getImplementationVersion()
                + " 全部可用字段数: " + analyzer.getAllPossibleFieldNames().size());

        for (Map.Entry<String, BotCase> bot : BOT_UAS.entrySet()) {
            UserAgent ua = analyzer.parse(bot.getValue().ua());
            System.out.println("\n=== [机器人] " + bot.getKey() + " ===");
            printNonNoiseFields(analyzer, ua);
            assertEquals(bot.getValue().expectedAgentClass(), ua.getValue(UserAgent.AGENT_CLASS), "AGENT_CLASS: " + bot.getKey());
            assertEquals(bot.getValue().expectedAgentName(), ua.getValue(UserAgent.AGENT_NAME), "AGENT_NAME: " + bot.getKey());
        }
        for (Map.Entry<String, HumanCase> human : HUMAN_UAS.entrySet()) {
            UserAgent ua = analyzer.parse(human.getValue().ua());
            System.out.println("\n=== [人类] " + human.getKey() + " ===");
            printNonNoiseFields(analyzer, ua);
            assertEquals(human.getValue().expectedAgentClass(), ua.getValue(UserAgent.AGENT_CLASS), "AGENT_CLASS: " + human.getKey());
            assertEquals(human.getValue().expectedDeviceClass(), ua.getValue(UserAgent.DEVICE_CLASS), "DEVICE_CLASS: " + human.getKey());
        }
    }

    /**
     * 机器人自报家门:合法爬虫在 UA 中声明联系邮箱/官网,可用于辅助识别真爬虫。
     * 伪造 UA 抄不到真实联系方式的用途,配合 IP 校验可防伪装。
     */
    @Test
    void extractsBotSelfDeclarationInfo() {
        UserAgentAnalyzer analyzer = allFieldsAnalyzer();
        UserAgent gptBot = analyzer.parse(BOT_UAS.get("GPTBot(OpenAI 训练采集)").ua());
        assertEquals("https://openai.com/gptbot", gptBot.getValue(UserAgent.AGENT_INFORMATION_URL));

        UserAgent claudeBot = analyzer.parse(BOT_UAS.get("ClaudeBot(Anthropic 训练采集)").ua());
        assertEquals("claudebot@anthropic.com", claudeBot.getValue(UserAgent.AGENT_INFORMATION_EMAIL));

        // 人类流量不带这些声明
        UserAgent chrome = analyzer.parse(HUMAN_UAS.get("Chrome 桌面").ua());
        assertEquals("Unknown", chrome.getValue(UserAgent.AGENT_INFORMATION_URL));
        assertEquals("Unknown", chrome.getValue(UserAgent.AGENT_INFORMATION_EMAIL));
    }

    /**
     * 用生产环境 UserAgentAnalyzerConfig(仅 8 个字段)验证:现有管线字段即可区分 AI 机器人。
     * 判定谓词:AGENT_CLASS=Robot 或 DEVICE_CLASS=Robot(覆盖 Claude-User 这种 AgentClass=Browser 的特例)。
     */
    @Test
    void productionConfigDetectsAiBots() {
        AbstractUserAgentAnalyzer analyzer = new UserAgentAnalyzerConfig().userAgentAnalyzer();
        for (Map.Entry<String, BotCase> bot : BOT_UAS.entrySet()) {
            UserAgent ua = analyzer.parse(bot.getValue().ua());
            boolean botSignal = "Robot".equals(ua.getValue(UserAgent.AGENT_CLASS))
                    || "Robot".equals(ua.getValue(UserAgent.DEVICE_CLASS));
            assertTrue(botSignal, "生产配置应能识别机器人信号: " + bot.getKey());
            System.out.printf("生产配置 %-40s -> AGENT_CLASS=%-10s AGENT_NAME=%-18s DEVICE_CLASS=%s%n",
                    bot.getKey(), ua.getValue(UserAgent.AGENT_CLASS), ua.getValue(UserAgent.AGENT_NAME), ua.getValue(UserAgent.DEVICE_CLASS));
        }
        for (Map.Entry<String, HumanCase> human : HUMAN_UAS.entrySet()) {
            UserAgent ua = analyzer.parse(human.getValue().ua());
            boolean botSignal = "Robot".equals(ua.getValue(UserAgent.AGENT_CLASS))
                    || "Robot".equals(ua.getValue(UserAgent.DEVICE_CLASS));
            assertTrue(!botSignal, "人类流量不应出现机器人信号: " + human.getKey());
        }
    }

    private static void printNonNoiseFields(UserAgentAnalyzer analyzer, UserAgent ua) {
        for (String field : analyzer.getAllPossibleFieldNamesSorted()) {
            String value = ua.getValue(field);
            if (value != null && !NOISE.contains(value)) {
                System.out.printf("  %-38s = %s%n", field, value);
            }
        }
    }
}
