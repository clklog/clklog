package com.zcunsoft.controller;


import com.zcunsoft.model.RawMessage;
import com.zcunsoft.services.IReceiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class KafkaController {

    @Resource
    private IReceiveService receiveService;

    @RequestMapping(value = "api/gp", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> gp(RawMessage rawMessage, HttpServletRequest request) {
        String response = receiveService.extractLog(rawMessage, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
