package com.zcunsoft.clklog.traffic;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** 验证 UA 直判、IP 网段与有效期匹配，以及非法规则的缓存回退。 */
class UaTrafficClassifierTest {
    private final UaTrafficClassifier classifier = new UaTrafficClassifier();
    private final UaRuleSet rules = UaRuleSet.compile("python-requests\n\nspider", false);

    @Test
    void routesOnlyExplicitSignals() {
        SearchEngineIpSnapshot empty = SearchEngineIpSnapshot.empty();
        assertFalse(classifier.classify("", "", "Unknown", "", "", rules, empty).isBot());
        assertTrue(classifier.classify("", "", "", "", "", UaRuleSet.compile("^$", false), empty).isBot());
        assertFalse(classifier.classify("Mozilla/5.0", "", "Desktop", "Chrome", "", rules, empty).isBot());
        assertFalse(classifier.classify("Mozilla/5.0 SogouExplorer/1.0", "", "Desktop", "", "", rules, empty).isBot());
        assertTrue(classifier.classify("python-requests/1.0", "", "", "", "", rules, empty).isBot());
        assertTrue(classifier.classify("x", "", "Robot", "", "", rules, empty).isBot());
        assertTrue(classifier.classify("HeadlessChrome/1.0", "", "", "", "", rules, empty).isBot());
        TrafficDecision google = classifier.classify("Googlebot/2.1", "", "Robot", "", "", rules, empty);
        assertEquals(100, google.getBotScore());
        assertEquals("google", google.getSearchEngine());
        assertEquals("unverified", google.getVerificationStatus());
        assertEquals("not_applicable", google.getAiVerificationStatus());
        assertEquals(Arrays.asList("ua_device_class", "search_engine_ua"), google.getReasons());
    }

    @Test
    void recognizesScriptAccess() {
        SearchEngineIpSnapshot empty = SearchEngineIpSnapshot.empty();
        String[][] scripts = {
                {"sqlmap/1.5.5#stable", "SECURITY_SCANNER"},
                {"Mozilla/5.0 zgrab/0.1.8", "SECURITY_SCANNER"},
                {"python-requests/2.31.0", "PYTHON"},
                {"Scrapy/2.11 (+https://scrapy.org)", "PYTHON"},
                {"curl/8.4.0", "COMMAND_LINE"},
                {"Wget/1.21.3", "COMMAND_LINE"},
                {"Apache-HttpClient/4.5.13 (Java/17.0.9)", "JAVA"},
                {"okhttp/4.12.0", "JAVA"},
                {"Go-http-client/1.1", "GO"},
                {"axios/1.6.2", "NODEJS"},
                {"Typhoeus - https://github.com/typhoeus/typhoeus", "RUBY"},
                {"Dart/3.2 (dart:io)", "DART"}};
        for (String[] script : scripts) {
            assertEquals(script[1], classifier.classifyScriptUa(script[0]), script[0]);
            TrafficDecision decision = classifier.classify(script[0], "", "", "", "", "", rules, empty);
            assertTrue(decision.isBot(), script[0]);
            assertTrue(decision.getReasons().contains("ua_script"), script[0]);
            assertEquals("", decision.getSearchEngine(), script[0]);
            assertEquals("not_applicable", decision.getAiVerificationStatus(), script[0]);
        }
        assertEquals("EMPTY_UA", classifier.classifyScriptUa(""));
        assertEquals("UNKNOWN", classifier.classifyScriptUa("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0"));
        // 搜索引擎与 AI 声明优先于脚本规则，不叠加 ua_script
        TrafficDecision google = classifier.classify("Googlebot/2.1", "", "", "", "", "", rules, empty);
        assertFalse(google.getReasons().contains("ua_script"));
        assertTrue(google.getReasons().contains("search_engine_ua"));
    }

    @Test
    void recognizesAllSearchEngineCrawlers() {
        SearchEngineIpSnapshot empty = SearchEngineIpSnapshot.empty();
        String[][] engines = {
                {"YisouSpider/5.0 (+https://www.sm.cn/help/spider)", "yisou"},
                {"Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)", "bing"},
                {"Mozilla/5.0 (compatible; BingPreview/1.0b; +http://www.bing.com/bingpreview.htm)", "bing"},
                {"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/534+ (KHTML, like Gecko) BingVideoPreview/1.0b", "bing"},
                {"msnbot/2.0b (+http://search.msn.com/msnbot.htm)", "bing"},
                {"Mozilla/5.0 (compatible; MicrosoftPreview/2.0; +https://www.bing.com/webmasters)", "bing"},
                {"Mozilla/5.0 (compatible; adidxbot/2.0; +http://www.bing.com/bingbot.htm)", "bing"},
                {"Mozilla/5.0 (compatible; HaoSouSpider; +https://www.so.com/help/help.html)", "360"},
                {"Mozilla/5.0 (compatible; 360Spider; +https://www.so.com/help/help.html)", "360"},
                {"Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)", "google"},
                {"Mozilla/5.0 (X11; Linux x86_64; Storebot-Google/1.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36", "google"},
                {"Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Google-InspectionTool/1.0; +https://search.google.com/test/website-friendly)", "google"},
                {"Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)", "baidu"},
                {"Mozilla/5.0 (compatible; Baiduspider-render/2.0; Renderer; +http://www.baidu.com/search/spider.html)", "baidu"},
                {"Mozilla/5.0 (compatible; Baiduspider-image/1.0; +http://www.baidu.com/search/spider.html)", "baidu"},
                {"Mozilla/5.0 (compatible; Baiduspider-cpro/1.0; +http://www.baidu.com/search/spider.html)", "baidu"},
                {"Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)", "yandex"},
                {"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)", "yandex"},
                {"Mozilla/5.0 (compatible; YandexAdditionalBot/1.0; +http://yandex.com/bots)", "yandex"},
                {"Sogou web spider/4.0(+https://www.sogou.com/docs/help/web.htm)", "sogou"},
                {"Sogou inst spider/4.0(+https://www.sogou.com/docs/help/inst.htm)", "sogou"},
                {"Sogou-Test-Spider/1.0", "sogou"},
                {"DuckDuckBot/1.1; (+http://duckduckgo.com/duckduckbot.html)", "duckduckgo"},
                {"Mozilla/5.0 (compatible; DuckDuckGo-Favicons-Bot/1.0; +http://duckduckgo.com/favicon_bot.html)", "duckduckgo"},
                {"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15 (Applebot/0.1; +http://www.apple.com/go/applebot)", "apple"},
                {"Mozilla/5.0 (Linux; Android 7.0;) AppleWebKit/537.36 (KHTML, like Gecko) Mobile Safari/537.36 (compatible; PetalBot; +https://webmaster.petalsearch.com/site/petalbot)", "huawei"},
                {"Mozilla/5.0 (compatible; Bravebot/1.0; +https://brave.com/search)", "brave"}};
        for (String[] engine : engines) {
            TrafficDecision decision = classifier.classify(engine[0], "", "", "", "", "", rules, empty);
            assertEquals(engine[1], decision.getSearchEngine(), engine[0]);
            assertTrue(decision.getReasons().contains("search_engine_ua"), engine[0]);
            assertEquals("not_applicable", decision.getAiVerificationStatus(), engine[0]);
        }
    }

    @Test
    void recognizesAiSourcesAndVerifiesAgainstLibrary() {
        long now = System.currentTimeMillis();
        UaRuleSet noRules = UaRuleSet.compile("", false);
        SearchEngineIpSnapshot library = new SearchEngineIpSnapshot(Collections.singletonList(
                new SearchEngineIpEntry("openai", "192.0.2.0/24", "official_feed", now - 10, now + 10000)));
        SearchEngineIpSnapshot empty = SearchEngineIpSnapshot.empty();

        TrafficDecision gpt = classifier.classify(
                "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko); compatible; GPTBot/1.1; +https://openai.com/gptbot",
                "192.0.2.1", "", "", "", "", noRules, library);
        assertEquals(Collections.singletonList("ua_ai_crawler"), gpt.getReasons());
        assertEquals("", gpt.getSearchEngine());
        assertEquals("not_applicable", gpt.getVerificationStatus());
        assertEquals("verified", gpt.getAiVerificationStatus());

        // AI 特征先于搜索引擎匹配：Applebot-Extended 包含 Applebot，仍归 AI 来源
        TrafficDecision appleExt = classifier.classify(
                "Mozilla/5.0 (compatible; Applebot-Extended/1.0; +https://support.apple.com/applebot)",
                "192.0.2.1", "", "", "", "", noRules, empty);
        assertEquals(Collections.singletonList("ua_ai_crawler"), appleExt.getReasons());
        assertEquals("", appleExt.getSearchEngine());
        assertEquals("unverified", appleExt.getAiVerificationStatus());

        // 字节爬虫由搜索引擎迁移至 AI 来源
        TrafficDecision bytespider = classifier.classify(
                "Mozilla/5.0 (compatible; Bytespider; https://zhanzhang.toutiao.com/)",
                "192.0.2.1", "", "", "", "", noRules, empty);
        assertEquals(Collections.singletonList("ua_ai_crawler"), bytespider.getReasons());
        assertEquals("", bytespider.getSearchEngine());
        assertEquals("unverified", bytespider.getAiVerificationStatus());

        // DuckAssistBot 归 DuckDuckGo AI 来源，不进搜索引擎分类
        TrafficDecision duckAssist = classifier.classify(
                "DuckAssistBot/1.2; (+http://duckduckgo.com/duckassistbot.html)",
                "192.0.2.1", "", "", "", "", noRules, empty);
        assertEquals(Collections.singletonList("ua_ai_crawler"), duckAssist.getReasons());
        assertEquals("", duckAssist.getSearchEngine());
        assertEquals("unverified", duckAssist.getAiVerificationStatus());
    }

    @Test
    void recognizesAgentAndDeviceRobotClasses() {
        SearchEngineIpSnapshot empty = SearchEngineIpSnapshot.empty();
        for (String agentClass : Arrays.asList("Robot", "Robot Mobile")) {
            TrafficDecision result = classifier.classify("example", "", "Desktop", agentClass, "", "", rules, empty);
            assertTrue(result.isBot(), agentClass);
            assertEquals(Collections.singletonList("ua_agent_class"), result.getReasons());
            assertEquals("Desktop", result.getDeviceClass());
        }
        for (String deviceClass : Arrays.asList("Robot", "Robot Mobile", "Robot Imitator", "Hacker")) {
            TrafficDecision result = classifier.classify("example", "", deviceClass, "Browser", "", "", rules, empty);
            assertTrue(result.isBot(), deviceClass);
            assertEquals(Collections.singletonList("ua_device_class"), result.getReasons());
            assertEquals(deviceClass, result.getDeviceClass());
        }
        TrafficDecision combined = classifier.classify("example", "", "Robot Mobile", "Robot", "", "", rules, empty);
        assertEquals(Arrays.asList("ua_device_class", "ua_agent_class"), combined.getReasons());
        assertFalse(classifier.classify("example", "", null, null, "", "", rules, empty).isBot());
        assertFalse(classifier.classify("example", "", "Desktop", "Browser", "", "", rules, empty).isBot());
        assertFalse(classifier.classify("example", "", "Desktop", "Robot Imitator", "", "", rules, empty).isBot());
        assertTrue(classifier.classify("example", "", "robot mobile", "browser", "", "", rules, empty).isBot());
    }

    @Test
    void matchesValidCidrAndExpiresAtLookup() {
        long now = System.currentTimeMillis();
        SearchEngineIpSnapshot snapshot = new SearchEngineIpSnapshot(Arrays.asList(
                new SearchEngineIpEntry("google", "192.0.2.0/24", "official_feed", now - 10, now + 10000),
                new SearchEngineIpEntry("bing", "2001:db8::/32", "official_feed", now - 10, now + 10000),
                new SearchEngineIpEntry("google", "198.51.100.0/24", "official_feed", now - 100, now - 1)));
        assertEquals("verified", snapshot.match("google", "192.0.2.1", now).getStatus());
        assertEquals("ua_mismatch", snapshot.match("google", "2001:db8::1", now).getStatus());
        assertEquals("unverified", snapshot.match("google", "198.51.100.1", now).getStatus());
        assertEquals("unverified", snapshot.match("google", "example.com", now).getStatus());
        assertEquals("not_applicable", snapshot.match("", "192.0.2.1", now).getStatus());
        assertEquals("unverified", new SearchEngineIpSnapshot(Collections.emptyList())
                .match("google", "192.0.2.1", now).getStatus());
    }

    @Test
    void rejectsMalformedRulesAndNetworks() {
        assertThrows(IllegalArgumentException.class, () -> UaRuleSet.compile("[", false));
        assertFalse(UaRuleSet.compile("[", true).matches("test"));
        assertThrows(IllegalArgumentException.class,
                () -> new SearchEngineIpEntry("google", "example.com/24", "source", 1, 2));
        UaRuleCache cache = new UaRuleCache();
        assertTrue(cache.get("p", "robot").matches("robot"));
        assertTrue(cache.get("p", "[").matches("robot"));
        assertFalse(cache.get("new", "[").matches("robot"));
    }
}
