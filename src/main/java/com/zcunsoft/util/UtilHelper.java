package com.zcunsoft.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

}
