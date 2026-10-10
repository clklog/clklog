package com.zcunsoft.clklog.sysmgmt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 首页
 */
@Controller
public class HomeController {

    /**
     * 访问首页
     *
     * @param request http请求
     * @return {@link String }
     */
    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        String domain = request.getServerName();
        if (domain.contains("localhost")) {
            return "redirect:" + "/doc.html";
        } else {
            return "redirect:" + "/authmanage/doc.html";
        }
    }
}
