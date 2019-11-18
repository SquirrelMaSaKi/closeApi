package com.rj.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test01Controller {
    @RequestMapping("/test/{name}")
    public String testInvoke(@PathVariable("name") String name) {
        return "test1=====>"+name;
    }
}
