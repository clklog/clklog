package com.zcunsoft.clklog.analysis;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.utils.ExtractUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AppTest {

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        String test1 = "1704532084140,clklogapp,123456,null,0,8.8.8.8,{\"identities\":{\"$identity_cookie_id\":\"1a7cc28c-52e1-40bb-b506-d86cdcfd7361\"},\"distinct_id\":\"1a7cc28c-52e1-40bb-b506-d86cdcfd7361\",\"lib\":{\"$lib\":\"js\",\"$lib_method\":\"code\",\"$lib_version\":\"1.25.6\"},\"properties\":{\"$timezone_offset\":-480,\"$screen_height\":780,\"$screen_width\":360,\"$viewport_height\":691,\"$viewport_width\":360,\"$lib\":\"js\",\"$lib_version\":\"1.25.6\",\"$latest_traffic_source_type\":\"直接流量\",\"$latest_search_keyword\":\"未取到值_直接打开\",\"$latest_referrer\":\"\",\"$title\":\"你好\",\"$url\":\"https://app.clklogapp.com/?time=&&event=bdstore#/all?tab=0\",\"$url_path\":\"/#/all\",\"$referrer_host\":\"app.clklogapp.com\",\"$referrer\":\"https://app.clklogapp.com/?time=1704531493267&&event=bdstore#/detail/?id=8a7581c78cd2f117018cd3bf5b941c19\",\"$viewport_position\":0,\"event_duration\":2.12,\"$is_first_day\":false,\"$latest_referrer_host\":\"\",\"$event_session_id\":\"18cddfe8ce25c40ecd1936b62f5685773254d28080018cddfe8ce3435\",\"$user_agent\":\"Mozilla/5.0 (Linux; Android 12; ANA-AN00 Build/HUAWEIANA-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/99.0.4844.88 Mobile Safari/537.36 Kuang/2.2.3\",\"$browser\":\"Chrome Webview\",\"$browser_version\":\"Chrome Webview 99.0.4844.88\",\"$model\":\"Huawei ANA-AN00\",\"$brand\":\"Huawei\",\"$manufacturer\":\"Huawei\",\"country\":\"中国\",\"province\":\"北京\",\"city\":\"北京\",\"raw_url\":\"https://app.clklogapp.com/?time=1704531493267&&event=bdstore#/all?tab=0\"},\"anonymous_id\":\"1a7cc28c-52e1-40bb-b506-d86cdcfd7361\",\"type\":\"track\",\"event\":\"$WebPageLeave\",\"time\":1704532083862,\"_track_id\":473513880,\"_flush_time\":1704532083880}";

        LogBean target1 = new LogBean();
        target1.setKafkaDataTime("1704532084140");
        target1.setProjectName("clklogapp");
        target1.setProjectToken("123456");
        target1.setCrc("null");
        target1.setIsCompress("0");
        target1.setClientIp("8.8.8.8");
        target1.setDistinctId("1a7cc28c-52e1-40bb-b506-d86cdcfd7361");
        target1.setLogTime("2024-01-06 17:08:03");
        target1.setStatDate("2024-01-06");
        target1.setStatHour("17");
        target1.setFlushTime("1704532083880");
        target1.setTypeContext("track");
        target1.setEvent("$WebPageLeave");
        target1.setTime("1704532083862");
        target1.setTrackId("473513880");
        target1.setIdentityCookieId("1a7cc28c-52e1-40bb-b506-d86cdcfd7361");
        target1.setLib("js");
        target1.setLibMethod("code");
        target1.setLibVersion("1.25.6");
        target1.setTimezoneOffset("-480");
        target1.setScreenHeight("780");
        target1.setScreenWidth("360");
        target1.setViewportHeight("691");
        target1.setViewportWidth("360");
        target1.setReferrer("https://app.clklogapp.com/?time=1704531493267&&event=bdstore#/detail/?id=8a7581c78cd2f117018cd3bf5b941c19");
        target1.setUrl("https://app.clklogapp.com/?time=&&event=bdstore#/all?tab=0");
        target1.setUrlPath("/#/all");
        target1.setTitle("你好");
        target1.setLatestSearchKeyword("未取到值_直接打开");
        target1.setLatestTrafficSourceType("直接流量");
        target1.setIsFirstDay("false");
        target1.setReferrerHost("app.clklogapp.com");
        target1.setCountry("中国");
        target1.setProvince("北京");
        target1.setCity("北京");
        target1.setBrand("Huawei");
        target1.setBrowser("Chrome Webview");
        target1.setBrowserVersion("Chrome Webview 99.0.4844.88");
        target1.setManufacturer("Huawei");
        target1.setModel("Huawei ANA-AN00");
        target1.setUserAgent("Mozilla/5.0 (Linux; Android 12; ANA-AN00 Build/HUAWEIANA-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/99.0.4844.88 Mobile Safari/537.36 Kuang/2.2.3");
        target1.setViewportPosition(0);
        target1.setEventDuration(2.12);
        target1.setIsLogined(0);
        target1.setEventSessionId("18cddfe8ce25c40ecd1936b62f5685773254d28080018cddfe8ce3435");
        target1.setCreateTime("2024-01-05 17:08:04");
        target1.setRawUrl("https://app.clklogapp.com/?time=1704531493267&&event=bdstore#/all?tab=0");
        Object[] targetArr = getFiledsInfo(target1).toArray();

        List<DynamicTest> dynamicTestList = new ArrayList<>();

        List<LogBean> logBeanList = ExtractUtil.extractToLogBean(test1);
        logBeanList.get(0).setCreateTime(target1.getCreateTime());
        Object[] resultArr = getFiledsInfo(logBeanList.get(0)).toArray();
        dynamicTestList.add(dynamicTest("test1 dynamic test", () -> Assertions.assertArrayEquals(targetArr, resultArr, "ok")));


        return dynamicTestList;
    }

    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {

            return null;
        }
    }

    private List<Object> getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List<Object> list = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            list.add(getFieldValueByName(fields[i].getName(), o));
        }
        return list;
    }
}
