package org.example.zupaybackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // This handles the main URL https://zupay-api.onrender.com/
    @GetMapping("/")
    public String check() {
        return "ZuPay System Online";
    }
}