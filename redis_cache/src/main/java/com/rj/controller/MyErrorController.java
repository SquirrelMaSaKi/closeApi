package com.rj.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyErrorController implements ErrorController {
    @RequestMapping("/error")
    public String error() {
        return "该服务找不到了！！！";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
