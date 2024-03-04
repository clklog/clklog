package com.zcunsoft.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.QueryCriteria;
import com.zcunsoft.util.GZIPUtils;
import com.zcunsoft.util.ObjectMapperUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

@RestController
public class KafkaController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final Logger storeLogger = LogManager.getLogger("com.zcunsoft.store");

    @Resource
    private ConstsDataHolder constsDataHolder;

    @Resource
    private ObjectMapperUtil objectMapper;

    @Resource
    private ReceiverSetting serverSettings;

    @RequestMapping(value = "api/gp", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Object> sendGet(QueryCriteria queryCriteria, HttpServletRequest request) {
        if (serverSettings.getAppList().contains(queryCriteria.getProject())) {
            String bodyString = getBodyString(request);
            String[] bodyStringList = bodyString.split("&");
            if (bodyStringList.length == 1 && !bodyString.equals(""))
                try {
                    JsonNode jsonNode = objectMapper.readTree(bodyString);

                    queryCriteria.setGzip(jsonNode.get("gzip").asText());
                    queryCriteria.setData_list(jsonNode.get("data_list").asText());
                    queryCriteria.setData(jsonNode.get("data").asText());
                    queryCriteria.setCrc(jsonNode.get("crc").asText());
                } catch (Exception exception) {
                }
            for (String s : bodyStringList) {
                String[] elm = s.split("=");
                if (elm[0].equals("data_list")) {
                    queryCriteria.setData_list(elm[1]);
                } else if (elm[0].equals("data")) {
                    queryCriteria.setData(elm[1]);
                } else if (elm[0].equals("crc")) {
                    queryCriteria.setCrc(elm[1]);
                } else if (elm[0].equals("gzip")) {
                    queryCriteria.setGzip(elm[1]);
                }
            }
            String ip = getIpAddr(request);
            try {
                String dataFinal, decodedString = null;
                if (queryCriteria.getData_list() != null) {
                    if (Pattern.matches(".*\\+.*|.*\\/.*|.*=.*", queryCriteria.getData_list())) {
                        decodedString = queryCriteria.getData_list();
                    } else {
                        decodedString = URLDecoder.decode(queryCriteria.getData_list());
                    }
                } else if (queryCriteria.getData() != null) {
                    if (Pattern.matches(".*\\+.*|.*\\/.*|.*=.*", queryCriteria.getData())) {
                        decodedString = queryCriteria.getData();
                    } else {
                        decodedString = URLDecoder.decode(queryCriteria.getData());
                    }
                } else {
                    logger.error("data为空");
                }
                if (decodedString != null) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    byte[] byteArrayNEW = decoder.decode(decodedString);

                    if (queryCriteria.getGzip().equals("1")) {
                        dataFinal = GZIPUtils.uncompressToString(byteArrayNEW);
                    } else {
                        dataFinal = new String(byteArrayNEW);
                    }
                    queryCriteria.setData(dataFinal);
                    queryCriteria.setClientIp(ip);
                    String ua = request.getHeader("user-agent");
                    if (ua == null) {
                        ua = request.getHeader("User-Agent");
                    }
                    queryCriteria.setUa(ua);

                    JsonNode json = objectMapper.readTree(dataFinal);
                    if (json instanceof ArrayNode) {
                        for (JsonNode jn : json) {
                            ObjectNode objectNode = ((ObjectNode) jn.get("properties"));
                            objectNode.put("$user_agent", ua);
                        }
                    } else {
                        ObjectNode objectNode = ((ObjectNode) json.get("properties"));
                        objectNode.put("$user_agent", ua);
                    }
                    dataFinal = objectMapper.writeValueAsString(json);
                    queryCriteria.setData(dataFinal);
                    constsDataHolder.getLogQueue().put(queryCriteria);
                    storeLogger.info(ip + "," + dataFinal);
                }
            } catch (Exception e) {
                String logData = queryCriteria.toString();
                logger.error(logData, e);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    public static String getBodyString(HttpServletRequest request) {
        ServletInputStream servletInputStream = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            servletInputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader((InputStream) servletInputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {

                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    if (inet != null) {
                        ipAddress = inet.getHostAddress();
                    }
                }
            }
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        return ipAddress;
    }
}
