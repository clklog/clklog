package com.zcunsoft.daemon;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.util.UtilHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoadConstProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ConstsDataHolder constsDataHolder;

    @Resource
    private ReceiverSetting serverSettings;


    Thread thread = null;

    boolean running = false;

    @PostConstruct
    public void start() throws Exception {

        running = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "LoadConstProcess");

        thread.start();
    }

    private void work() {
        try {
            loadCountry();
            loadProvince();
            loadCity();
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }

    }

    private void loadCity() {
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

    private void loadProvince() {
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

    private void loadCountry() {
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


    @PreDestroy
    public void stop() {
        running = false;

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (logger.isInfoEnabled()) {
            logger.info(thread.getName() + " stopping...");
        }
    }
}