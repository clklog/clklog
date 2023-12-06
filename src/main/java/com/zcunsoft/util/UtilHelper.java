package com.zcunsoft.util;

import com.sun.jndi.toolkit.url.Uri;
import com.zcunsoft.model.Rule;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc

/**
 * The Class UtilHelper.
 *
 * @author yuhao
 */
public final class UtilHelper {


    /**
     * Load file content.
     *
     * @param filePath the file path
     * @return the list
     */
    public static List<String> loadFileAllLine(String filePath) {
        InputStreamReader fr = null;

        File f = new File(filePath);

        List<String> lineList = null;
        try {
            fr = new InputStreamReader(new FileInputStream(f), "GB2312");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (fr != null) {
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            try {
                lineList = new ArrayList<String>();
                while ((line = br.readLine()) != null) {
                    lineList.add(line);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                fr.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return lineList;

    }

    public static void writeFileLocal(String path, String content, String encoding) {

        File file = new File(path);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);
            fileWriter.write(content);
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static String parseUrl(String rawurl, ConcurrentMap<String, Rule> htUrlReg) {
        String[] urlPairArray = rawurl.split("((?=[?#/&])|(?<=[?#/&]))", -1);
        StringBuilder parsedUrl = new StringBuilder();
        HashMap<String, String> delimiterMap = new HashMap<>();
        delimiterMap.put("/", "/");
        delimiterMap.put("?", "？");
        delimiterMap.put("&", "&");
        delimiterMap.put("#", "#");

        for (String urlPair : urlPairArray) {
            if (delimiterMap.containsKey(urlPair)) {
                parsedUrl.append(urlPair);
            } else {
                String parseUrlPair = urlPair;
                for (Map.Entry<String, Rule> urlReg : htUrlReg.entrySet()) {
                    if (urlReg.getValue().getType().equalsIgnoreCase("replace")) {
                        parseUrlPair = parseUrlPair.replaceAll(urlReg.getValue().getValue(), "{" + urlReg.getKey() + "}");
                    } else if (urlReg.getValue().getType().equalsIgnoreCase("remove")) {
                        parseUrlPair = parseUrlPair.replaceAll(urlReg.getValue().getValue(), "");
                    }
                }
                parsedUrl.append(parseUrlPair);
            }
        }
        return parsedUrl.toString();
    }

    public static String parseUrlPath(String rawUrl) {
        String path = File.separator;

        try {
            int index = rawUrl.lastIndexOf("#");
            if (index != -1) {
                int index2 = rawUrl.indexOf("?", index + 1);
                if (index2 != -1) {
                    path = rawUrl.substring(index, index2);

                } else {
                    path = rawUrl.substring(index);
                }
            } else {
                Uri uri = new Uri(rawUrl);
                path = uri.getPath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}
