package com.example.SpringSecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @GetMapping("user")
    public String usertest(){
        return "user api works";
    }

    @GetMapping("dev")
    public String devloptest(){
        return "admin api works";
    }
}
