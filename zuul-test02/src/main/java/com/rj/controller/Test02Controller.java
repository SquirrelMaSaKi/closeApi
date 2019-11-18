package com.rj.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test02Controller {
    @RequestMapping("/test02/{name}/{age}")
    public String testInvoke02(@PathVariable("name") String name, @PathVariable("age") int age) {
        return "test02=====>姓名是："+name+"，年龄是："+age;
    }
}
