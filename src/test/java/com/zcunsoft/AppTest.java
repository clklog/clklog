package com.zcunsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zcunsoft.model.Rule;
import com.zcunsoft.util.ReceiverObjectMapper;
import com.zcunsoft.util.UtilHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Unit test for simple App.
 */
public class AppTest {

    static ConcurrentMap<String, Rule> htForUrlRules;

    private static final TypeReference<Rule> ruleTypeReference = new TypeReference<Rule>() {
    };

    @BeforeAll
    protected static void setUp() throws Exception {
        List<String> ruleList = UtilHelper
                .loadFileAllLine(System.getProperty("user.dir") + File.separator + "rules" + File.separator
                        + "url.rules");

        htForUrlRules = new ConcurrentHashMap<>();
        ReceiverObjectMapper objectMapper = new ReceiverObjectMapper();
        for (String line : ruleList) {
            try {

                Rule rule = objectMapper.readValue(line,
                        ruleTypeReference);
                htForUrlRules.put(rule.getObject(), rule);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @TestFactory
    Collection<DynamicTest> testParseUrl() throws IOException {
        List<DynamicTest> dynamicTestList = new ArrayList<>();

        File file = new File("src/test/resources/url_test.txt");

        List<String> urlList = Files.readAllLines(file.toPath());

        for (int i = 0; i < urlList.size(); i++) {
            String[] array = urlList.get(i).split(",", -1);
            String parsed = UtilHelper.parseUrl(array[0], htForUrlRules);
            dynamicTestList.add(dynamicTest("test" + i, () -> Assertions.assertEquals(parsed, array[1], "ok")));
        }
        return dynamicTestList;
    }
}
