package com.zcunsoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.dto.QueryCriteria;
import com.zcunsoft.dto.Region;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.Rule;
import com.zcunsoft.util.ReceiverObjectMapper;
import com.zcunsoft.util.UtilHelper;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.AgentField;
import nl.basjes.parse.useragent.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

@Service
public class ReceiveServiceImpl implements IReceiveService {

    private final InetAddressValidator validator = InetAddressValidator.getInstance();

    private final ConstsDataHolder constsDataHolder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReceiverObjectMapper objectMapper;


    private final StringRedisTemplate queueRedisTemplate;


    private final AbstractUserAgentAnalyzer userAgentAnalyzer;


    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ReceiverSetting serverSettings;

    private final IP2Location locIpV4 = new IP2Location();
    private final IP2Location locIpV6 = new IP2Location();

    private final TypeReference<Rule> ruleTypeReference = new TypeReference<Rule>() {
    };

    public ReceiveServiceImpl(ConstsDataHolder constsDataHolder, ReceiverObjectMapper objectMapper, StringRedisTemplate queueRedisTemplate, AbstractUserAgentAnalyzer userAgentAnalyzer, KafkaTemplate<String, String> kafkaTemplate, ReceiverSetting serverSettings) {
        this.objectMapper = objectMapper;
        this.queueRedisTemplate = queueRedisTemplate;
        this.userAgentAnalyzer = userAgentAnalyzer;
        this.kafkaTemplate = kafkaTemplate;
        this.serverSettings = serverSettings;
        this.constsDataHolder = constsDataHolder;
        String binIpV4file = System.getProperty("user.dir") + File.separator + "iplib" + File.separator + "IP2LOCATION-LITE-DB3.BIN";

        try {
            locIpV4.Open(binIpV4file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String binIpV6file = System.getProperty("user.dir") + File.separator + "iplib" + File.separator + "IP2LOCATION-LITE-DB3.IPV6.BIN";

        try {
            locIpV6.Open(binIpV6file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Region analysisRegionFromIp(String clientIp) {
        Region region = new Region();
        region.setClientIp(clientIp);
        String regionInfo = (String) queueRedisTemplate.opsForHash().get("ClientIpRegionHash", clientIp);
        if (regionInfo == null) {
            IPResult rec = null;
            if (validator.isValidInet4Address(clientIp)) {
                rec = analysisIp(true, clientIp);
            } else if (validator.isValidInet6Address(clientIp)) {
                rec = analysisIp(false, clientIp);
            }

            if (rec != null && rec.getStatus().equalsIgnoreCase("OK")) {
                String country = rec.getCountryShort().toLowerCase(Locale.ROOT);
                String province = rec.getRegion().toLowerCase(Locale.ROOT);
                String city = rec.getCity().toLowerCase(Locale.ROOT);
                if ("-".equalsIgnoreCase(country)) {
                    country = "";
                }
                if ("-".equalsIgnoreCase(province)) {
                    province = "";
                }
                if ("-".equalsIgnoreCase(city)) {
                    city = "";
                }
                if (StringUtils.isNotBlank(country)) {
                    if (country.equalsIgnoreCase("TW")) {
                        country = "cn";
                        province = "taiwan";
                    }
                    if (country.equalsIgnoreCase("hk")) {
                        country = "cn";
                        province = "hongkong";
                        city = "hongkong";
                    }
                    if (country.equalsIgnoreCase("mo")) {
                        country = "cn";
                        province = "macau";
                        city = "macau";
                    }
                    if (constsDataHolder.getHtForCountry().containsKey(country)) {
                        country = constsDataHolder.getHtForCountry().get(country);
                    }
                }
                if (StringUtils.isNotBlank(province)) {
                    if (constsDataHolder.getHtForProvince().containsKey(province)) {
                        province = constsDataHolder.getHtForProvince().get(province);
                    }
                }
                if (StringUtils.isNotBlank(city)) {
                    if (constsDataHolder.getHtForCity().containsKey(city)) {
                        city = constsDataHolder.getHtForCity().get(city);
                    }
                }

                region.setCountry(country);
                region.setProvince(province);
                region.setCity(city);
            }
        } else {
            String[] regionArr = regionInfo.split(",", -1);
            if (regionArr.length == 4) {
                region.setCountry(regionArr[1]);
                region.setProvince(regionArr[2]);
                region.setCity(regionArr[3]);
            }
        }
        return region;
    }

    private IPResult analysisIp(boolean isIpV4, String clientIp) {
        IPResult rec = null;
        try {
            if (isIpV4) {
                rec = locIpV4.IPQuery(clientIp);
            } else {
                rec = locIpV6.IPQuery(clientIp);
            }
            //    loc.Close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rec;
    }

    @Override
    public String analysisData(QueryCriteria queryCriteria) {
        String dataFinal = queryCriteria.getData();
        Region region = analysisRegionFromIp(queryCriteria.getClientIp());
        String ua = queryCriteria.getUa();
        try {
            JsonNode array = objectMapper.readTree(dataFinal);
            if (array.isArray()) {
                for (JsonNode objNode : array) {
                    setProperty(objNode, ua, region);
                }
                dataFinal = objectMapper.writeValueAsString(array);
            } else {
                setProperty(array, ua, region);
                dataFinal = objectMapper.writeValueAsString(array);
            }
        } catch (Exception ex) {
            logger.error("analysisData err", ex);
        }

        return dataFinal;
    }

    @Override
    public void enqueueKafka(List<QueryCriteria> queryCriteriaList) {

        for (QueryCriteria queryCriteria : queryCriteriaList) {
            String dataFinal = analysisData(queryCriteria);
            if (StringUtils.isNotBlank(dataFinal)) {
                String logData = String.valueOf(System.currentTimeMillis()) + ',' + queryCriteria.getProject() + ',' + queryCriteria.getToken() + ',' + queryCriteria.getCrc() + ',' + queryCriteria.getGzip() + ',' + queryCriteria.getClientIp() + ',' + dataFinal;

                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(serverSettings.getTopicName(),
                        logData);
                future.addCallback(o -> {
                }, err -> logger.error("发送失败,", err));
            }
        }
    }

    private void setProperty(JsonNode jsonObject, String ua, Region region) {
        try {
            if (jsonObject.get("properties") != null) {
                UserAgent userAgent = userAgentAnalyzer.parse(ua);
                JsonNode prop = jsonObject.get("properties");
                ObjectNode objectNode = ((ObjectNode) prop);
                objectNode.put("$user_agent", ua);
                String browser = "";
                AgentField browserField = userAgent.get(UserAgent.AGENT_NAME);
                if (!browserField.isDefaultValue()) {
                    browser = browserField.getValue();
                }
                JsonNode jnBrowser = prop.get("$browser");
                if (StringUtils.isNotBlank(browser) && (jnBrowser == null || StringUtils.isBlank(jnBrowser.asText()))) {
                    objectNode.put("$browser", browser);
                }

                String browserVersion = "";
                AgentField browserVersionField = userAgent.get(UserAgent.AGENT_NAME_VERSION);
                if (!browserVersionField.isDefaultValue()) {
                    browserVersion = browserVersionField.getValue();
                }
                JsonNode jnBrowserVersion = prop.get("$browser_version");
                if (StringUtils.isNotBlank(browser) && (jnBrowserVersion == null || StringUtils.isBlank(jnBrowserVersion.asText()))) {
                    objectNode.put("$browser_version", browserVersion);
                }

                String model = "";
                AgentField deviceName = userAgent.get(UserAgent.DEVICE_NAME);
                if (!deviceName.isDefaultValue()) {
                    model = deviceName.getValue();
                }
                JsonNode jnModel = prop.get("$model");
                if (StringUtils.isNotBlank(model) && (jnModel == null || StringUtils.isBlank(jnModel.asText()))) {
                    objectNode.put("$model", model);
                }

                String brand = "";
                AgentField deviceBrand = userAgent.get(UserAgent.DEVICE_BRAND);
                if (!deviceBrand.isDefaultValue()) {
                    brand = deviceBrand.getValue();
                }
                JsonNode jnBrand = prop.get("$brand");
                if (StringUtils.isNotBlank(brand) && (jnBrand == null || StringUtils.isBlank(jnBrand.asText()))) {
                    objectNode.put("$brand", brand);
                }
                JsonNode jnManu = prop.get("$manufacturer");
                if (jnManu == null || StringUtils.isBlank(jnManu.asText())) {
                    objectNode.put("$manufacturer", brand);
                }

                //ip address info
                objectNode.put("country", region.getCountry());
                objectNode.put("province", region.getProvince());
                objectNode.put("city", region.getCity());

                // parse url
                JsonNode jnUrl = objectNode.get("$url");
                if (jnUrl != null) {
                    String rawUrl = jnUrl.asText();
                    System.out.println(rawUrl);
                    objectNode.put("raw_url", rawUrl);
                    String parsedUrl = UtilHelper.parseUrl(rawUrl, constsDataHolder.getHtUrlReg());
                    objectNode.put("$url", parsedUrl);

                    // parse url_path
                    JsonNode jnUrlPath = objectNode.get("$url_path");
                    if (jnUrlPath != null && "js".equalsIgnoreCase(objectNode.get("$lib").asText())) {
                        String rawUrlPath = jnUrlPath.asText();
                        if ("/".equalsIgnoreCase(rawUrlPath) || StringUtils.isBlank(rawUrlPath)) {
                            String parsedUrlPath = UtilHelper.parseUrlPath(rawUrl);
                            objectNode.put("$url_path", parsedUrlPath);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("setProperty err,", ex);
        }
    }

    @Override
    public void loadCity() {
        List<String> lineCityList = UtilHelper
                .loadFileAllLine(System.getProperty("user.dir") + File.separator + "iplib" + File.separator
                        + "chinacity.txt");

        ConcurrentMap<String, String> htForCity = constsDataHolder.getHtForCity();
        for (String line : lineCityList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForCity.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

    @Override
    public void loadProvince() {
        List<String> lineProvinceList = UtilHelper
                .loadFileAllLine(System.getProperty("user.dir") + File.separator + "iplib" + File.separator
                        + "chinaprovince.txt");

        ConcurrentMap<String, String> htForProvince = constsDataHolder.getHtForProvince();
        for (String line : lineProvinceList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForProvince.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

    @Override
    public void loadCountry() {
        List<String> countryList = UtilHelper
                .loadFileAllLine(System.getProperty("user.dir") + File.separator + "iplib" + File.separator
                        + "country.txt");

        ConcurrentMap<String, String> htForCountry = constsDataHolder.getHtForCountry();
        for (String line : countryList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForCountry.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

    @Override
    public void loadUrlRule() {
        List<String> ruleList = UtilHelper
                .loadFileAllLine(System.getProperty("user.dir") + File.separator + "rules" + File.separator
                        + "url.rules");
        ConcurrentMap<String, Rule> htForUrlRules = constsDataHolder.getHtUrlReg();
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
}
