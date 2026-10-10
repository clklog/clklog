package com.zcunsoft.clklog.analysis.utils;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.zcunsoft.clklog.analysis.bean.Region;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public class IPUtil implements Serializable {

    private String ipFileLocation = "";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IP2Location locIpV4 = new IP2Location();
    private final IP2Location locIpV6 = new IP2Location();

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
            logger.error("analysisIp error ", e);
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

        if (rec != null && "OK".equalsIgnoreCase(rec.getStatus())) {
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
            }
            region.setCountry(country);
            region.setProvince(province);
            region.setCity(city);
        }
        return region;
    }
}
