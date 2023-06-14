package com.example.newfieldpasser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String HelloJenkins() {
        return "hello! jenkins!";
    }
}
