package com.zcunsoft.cfg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.clklog.traffic.TrafficDecision;
import com.zcunsoft.clklog.traffic.UaRuleSet;
import com.zcunsoft.clklog.traffic.UaTrafficClassifier;
import com.zcunsoft.model.LogBean;
import com.zcunsoft.model.RawMessage;
import com.zcunsoft.model.Region;
import com.zcunsoft.util.ExtractUtil;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证接收端配置的真实 yauaa 解析器与实时分流规则能够协同工作。 */
class UserAgentAnalyzerConfigTest {
    @Test
    void classifiesBrowserSearchEngineAndHeadlessUserAgents() {
        AbstractUserAgentAnalyzer analyzer = new UserAgentAnalyzerConfig().userAgentAnalyzer();
        String browser = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        String googlebot = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
        String headless = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) HeadlessChrome/120.0.0.0 Safari/537.36";

        assertEquals("Desktop", analyzer.parse(browser).getValue(UserAgent.DEVICE_CLASS));
        assertFalse(classify(analyzer, browser).isBot());
        assertEquals("Robot", analyzer.parse(googlebot).getValue(UserAgent.DEVICE_CLASS));
        assertEquals("Robot", analyzer.parse(googlebot).getValue(UserAgent.AGENT_CLASS));
        TrafficDecision searchEngine = classify(analyzer, googlebot);
        assertTrue(searchEngine.isBot());
        assertEquals("google", searchEngine.getSearchEngine());
        assertEquals("unverified", searchEngine.getVerificationStatus());
        assertTrue(classify(analyzer, headless).getReasons().contains("ua_headless"));
    }

    @Test
    void extractsAgentAndDeviceClassWithOrWithoutProperties() throws Exception {
        AbstractUserAgentAnalyzer analyzer = new UserAgentAnalyzerConfig().userAgentAnalyzer();
        RawMessage raw = new RawMessage();
        raw.setUa("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        Region region = new Region();
        region.setClientIp("192.0.2.1");
        ObjectMapper mapper = new ObjectMapper();
        for (String json : new String[]{"{}", "{\"properties\":{}}"}) {
            LogBean event = ExtractUtil.extractToLogBean(mapper.readTree(json), analyzer, null, region, raw);
            assertEquals("Robot", event.getAgentClass());
            assertEquals("Robot", event.getDeviceClass());
        }
    }

    private static TrafficDecision classify(AbstractUserAgentAnalyzer analyzer, String ua) {
        UserAgent parsed = analyzer.parse(ua);
        return new UaTrafficClassifier().classify(ua, "192.0.2.1",
                parsed.getValue(UserAgent.DEVICE_CLASS), parsed.getValue(UserAgent.AGENT_CLASS),
                parsed.getValue(UserAgent.AGENT_NAME),
                parsed.getValue(UserAgent.OPERATING_SYSTEM_NAME), UaRuleSet.compile("", false),
                SearchEngineIpSnapshot.empty());
    }
}
