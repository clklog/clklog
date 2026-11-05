package com.zcunsoft.clklog.traffic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        assertEquals(Arrays.asList("ua_device_class", "search_engine_ua"), google.getReasons());
    }

    @Test
    void matchesValidCidrAndExpiresAtLookup() {
        long now = System.currentTimeMillis();
        SearchEngineIpSnapshot snapshot = new SearchEngineIpSnapshot(Arrays.asList(
                new SearchEngineIpEntry("google", "192.0.2.0/24", "official_feed", now - 10, now + 10000, "v1"),
                new SearchEngineIpEntry("bing", "2001:db8::/32", "official_feed", now - 10, now + 10000, "v2"),
                new SearchEngineIpEntry("google", "198.51.100.0/24", "official_feed", now - 100, now - 1, "v0")));
        assertEquals("verified", snapshot.match("google", "192.0.2.1", now).getStatus());
        assertEquals("ua_mismatch", snapshot.match("google", "2001:db8::1", now).getStatus());
        assertEquals("unverified", snapshot.match("google", "198.51.100.1", now).getStatus());
        assertEquals("unverified", snapshot.match("google", "example.com", now).getStatus());
        assertEquals("not_applicable", snapshot.match("", "192.0.2.1", now).getStatus());
        assertEquals("unverified", new SearchEngineIpSnapshot(Collections.emptyList())
                .match("google", "192.0.2.1", now).getStatus());
    }

    @Test
    void summarizesMixedVersionsAndBoundsLogOutput() {
        assertEquals("[] (distinct=0)", SearchEngineIpSnapshot.empty().getVersionSummary());
        List<SearchEngineIpEntry> entries = new ArrayList<>();
        for (int i = 10; i >= 0; i--) {
            entries.add(new SearchEngineIpEntry("google", "192.0.2.0/24", "source", 1, 2,
                    String.format("v%02d", i)));
        }
        entries.add(new SearchEngineIpEntry("bing", "2001:db8::/32", "source", 1, 2, "v00"));
        SearchEngineIpSnapshot mixed = new SearchEngineIpSnapshot(entries);
        assertEquals("[v00, v01, v02, v03, v04, v05, v06, v07, v08, v09] (distinct=11)",
                mixed.getVersionSummary());
        SearchEngineIpSnapshot unsafe = new SearchEngineIpSnapshot(Collections.singletonList(
                new SearchEngineIpEntry("google", "192.0.2.0/24", "source", 1, 2,
                        "v\n" + String.join("", Collections.nCopies(100, "x")))));
        assertFalse(unsafe.getVersionSummary().contains("\n"));
        assertTrue(unsafe.getVersionSummary().contains("..."));
    }

    @Test
    void rejectsMalformedRulesAndNetworks() {
        assertThrows(IllegalArgumentException.class, () -> UaRuleSet.compile("[", false));
        assertFalse(UaRuleSet.compile("[", true).matches("test"));
        assertThrows(IllegalArgumentException.class,
                () -> new SearchEngineIpEntry("google", "example.com/24", "source", 1, 2, "v1"));
        UaRuleCache cache = new UaRuleCache();
        assertTrue(cache.get("p", "robot").matches("robot"));
        assertTrue(cache.get("p", "[").matches("robot"));
        assertFalse(cache.get("new", "[").matches("robot"));
    }
}
