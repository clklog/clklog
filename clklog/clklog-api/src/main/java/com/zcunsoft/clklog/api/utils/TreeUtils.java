package com.zcunsoft.clklog.api.utils;

import com.zcunsoft.clklog.api.models.visituri.SplitUriPathInfo;
import com.zcunsoft.clklog.api.models.visituri.UriPathNode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 构建url的树形工具.
 */
public class TreeUtils {
    /**
     * 分割路径.
     *
     * @param uriPath 路径
     * @return 路径层次信息
     */
    public static SplitUriPathInfo splitUriPath(String uriPath) {
        SplitUriPathInfo splitUriPathInfo = new SplitUriPathInfo();

        /*  路径层次分隔符 */
        String dot = ".";
        String slash = "/";

        if (uriPath.startsWith(slash)) {
            String[] slashArr = uriPath.split(slash, -1);
            splitUriPathInfo.setUriPathArr(slashArr);
            splitUriPathInfo.setSplitChar(slash);
        } else {
            if(uriPath.equalsIgnoreCase("N/A")) {
                splitUriPathInfo.setUriPathArr(new String[]{uriPath});
                splitUriPathInfo.setSplitChar(dot);
            }
            else {
                String[] dotArr = uriPath.split("\\" + dot, -1);
                String[] slashArr = uriPath.split(slash, -1);

                if (slashArr.length == 1) {
                    splitUriPathInfo.setUriPathArr(dotArr);
                    splitUriPathInfo.setSplitChar(dot);
                } else {
                    splitUriPathInfo.setUriPathArr(slashArr);
                    splitUriPathInfo.setSplitChar(slash);
                }
            }
        }
        return splitUriPathInfo;
    }

    /**
     * 构建树形.
     *
     * @param uriPathList 路径列表
     * @param host        域名
     * @return 树形节点列表
     */
    public static List<UriPathNode> buildTree(List<String> uriPathList, String host) {
        uriPathList = uriPathList.stream().sorted().collect(Collectors.toList());

        HashMap<String, UriPathNode> map = new HashMap<>();
        for (String urlPath : uriPathList) {
            SplitUriPathInfo splitUriPathInfo = splitUriPath(urlPath);
            String[] urlPathArr = splitUriPathInfo.getUriPathArr();
            for (int i = 0; i < urlPathArr.length; i++) {
                int j = 0;
                String tempUriPath = "";
                String parentUriPath = "";
                String segment = "";
                do {
                    parentUriPath = tempUriPath;
                    if (StringUtils.isNotBlank(tempUriPath)) {
                        tempUriPath += splitUriPathInfo.getSplitChar();
                    }
                    tempUriPath += urlPathArr[j];
                    segment = urlPathArr[j];
                    j++;
                }
                while (j <= i);

                if (!map.containsKey(tempUriPath)) {
                    UriPathNode uriPathNode = new UriPathNode();
                    uriPathNode.setUriPath(tempUriPath);
                    uriPathNode.setParentUriPath(parentUriPath);
                    uriPathNode.setHost(host);
                    uriPathNode.setSegment(segment);
                    uriPathNode.setOriginalUriPath(new ArrayList<>(Arrays.asList(urlPath)));
                    uriPathNode.setSplitChar(splitUriPathInfo.getSplitChar());
                    if (urlPath.startsWith(splitUriPathInfo.getSplitChar())) {
                        uriPathNode.setStartSplitChar(true);
                    }
                    map.put(tempUriPath, uriPathNode);
                } else {
                    UriPathNode uriPathNode = map.get(tempUriPath);
                    if (!uriPathNode.getOriginalUriPath().contains(urlPath)) {
                        uriPathNode.getOriginalUriPath().add(urlPath);
                    }
                }
            }
        }

        List<UriPathNode> uriPathNodeList = new ArrayList<>(map.values());

        List<UriPathNode> tree = new ArrayList<>();
        HashMap<String, UriPathNode> mappedArr = new HashMap<>();

        uriPathNodeList.forEach(f -> {
            String name = f.getUriPath();
            if (!mappedArr.containsKey(name)) {
                f.setLeaves(new ArrayList<>());
                mappedArr.put(name, f);
            }
        });

        for (Map.Entry<String, UriPathNode> item : mappedArr.entrySet()) {
            String id = item.getKey();
            if (mappedArr.containsKey(id)) {
                UriPathNode mappedElem = mappedArr.get(id);
                if (StringUtils.isNotBlank(mappedElem.getParentUriPath())) {
                    String parentId = mappedElem.getParentUriPath();
                    mappedArr.get(parentId).getLeaves().add(mappedElem);
                } else {
                    tree.add(mappedElem);
                }
            }
        }
        return tree;
    }
}
