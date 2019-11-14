package com.rj.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyErrorController implements ErrorController {
    @RequestMapping("/error")
    public String error() {
        return "服务找不到";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
