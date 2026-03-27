package org.example.zupaybackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // This now handles BOTH GET and HEAD requests for the main URL
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String check() {
        return "ZuPay System Online";
    }
}