package com.zcunsoft.clklog.manage.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 文件后缀过滤器.
 */
public class ExtensionFilter implements FilenameFilter {

    /**
     * 后缀名.
     */
    String ext;

    /**
     * Instantiates a new Extension filter.
     *
     * @param ext the ext
     */
    public ExtensionFilter(String ext) {
        this.ext = "." + ext;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(ext);
    }
}
