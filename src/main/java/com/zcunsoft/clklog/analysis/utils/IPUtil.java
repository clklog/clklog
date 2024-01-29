package com.zcunsoft.clklog.analysis.utils;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.zcunsoft.clklog.analysis.bean.Region;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IPUtil implements Serializable {

    private String ipFileLocation = "";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IP2Location locIpV4 = new IP2Location();
    private final IP2Location locIpV6 = new IP2Location();

    private final ConcurrentMap<String, String> htForCountry = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, String> htForProvince = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, String> htForCity = new ConcurrentHashMap<String, String>();

    private final InetAddressValidator validator = InetAddressValidator.getInstance();

    public IPUtil(String ipFileLocation) {
        this.ipFileLocation = ipFileLocation;
    }

    public void loadIpFile() throws IOException {
        String binIpV4file = ipFileLocation + File.separator + "iplib" + File.separator + "IP2LOCATION-LITE-DB3.BIN";
        logger.info("binIpV4file " + binIpV4file);
        try {
            locIpV4.Open(binIpV4file, true);
        } catch (IOException e) {
            logger.error("load ipv4file error " + e.getMessage());
        }
        String binIpV6file = ipFileLocation + File.separator + "iplib" + File.separator + "IP2LOCATION-LITE-DB3.IPV6.BIN";

        try {
            locIpV6.Open(binIpV6file, true);
        } catch (IOException e) {
            logger.error("load ipv6file error " + e.getMessage());
        }

        loadCountry();
        loadProvince();
        loadCity();
    }

    public IPResult analysisIp(boolean isIpV4, String clientIp) {
        IPResult rec = null;
        try {
            if (isIpV4) {
                rec = locIpV4.IPQuery(clientIp);
            } else {
                rec = locIpV6.IPQuery(clientIp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rec;
    }

    public Region analysisRegionFromIp(String clientIp) {
        Region region = new Region();
        region.setClientIp(clientIp);

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
                if (htForCountry.containsKey(country)) {
                    country = htForCountry.get(country);
                }
            }
            if (StringUtils.isNotBlank(province)) {
                if (htForProvince.containsKey(province)) {
                    province = htForProvince.get(province);
                }
            }
            if (StringUtils.isNotBlank(city)) {
                if (htForCity.containsKey(city)) {
                    city = htForCity.get(city);
                }
            }
            region.setCountry(country);
            region.setProvince(province);
            region.setCity(city);
        }
        return region;
    }

    private void loadCity() throws IOException {
        List<String> lineCityList = FileUtils.readLines(new File(
                (ipFileLocation + File.separator + "iplib" + File.separator
                        + "chinacity.txt")), Charset.forName("GB2312"));

        for (String line : lineCityList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForCity.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

    private void loadProvince() throws IOException {
        List<String> lineProvinceList = FileUtils.readLines(new File(ipFileLocation + File.separator + "iplib" + File.separator
                + "chinaprovince.txt"), Charset.forName("GB2312"));
        for (String line : lineProvinceList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForProvince.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

    private void loadCountry() throws IOException {
        List<String> countryList = FileUtils.readLines(new File(ipFileLocation + File.separator + "iplib" + File.separator
                + "country.txt"), Charset.forName("GB2312"));

        for (String line : countryList) {

            String[] pair = line.split(",");
            if (pair.length >= 2) {
                htForCountry.put(pair[0].toLowerCase(Locale.ROOT), pair[1]);
            }
        }
    }

}
