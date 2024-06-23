package com.zcunsoft.util;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class IOUtil {

    private final static Logger logger = LoggerFactory.getLogger(IOUtil.class);

    public static String readFile(String filePath) {
        StringBuilder fileContent = new StringBuilder();

        FileInputStream fis = null;
        BOMInputStream bomIn = null;
        InputStreamReader inputStreamReader = null;

        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            //可检测多种类型，并剔除bom
            bomIn = new BOMInputStream(fis, false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE);
            String charset = "utf-8";

            //若检测到bom，则使用bom对应的编码
            if (bomIn.hasBOM()) {
                charset = bomIn.getBOMCharsetName();
            }

            inputStreamReader = new InputStreamReader(bomIn, charset);

            char[] buffer = new char[1024];
            int len;
            while ((len = inputStreamReader.read(buffer)) != -1) {
                String content = new String(buffer, 0, len);
                fileContent.append(content);
            }
        } catch (Exception ex) {
            logger.error("readFile error", ex);
        } finally {

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bomIn != null) {
                try {
                    bomIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent.toString();
    }
}
