package com.rj.controller;

import com.rj.service.SearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/es")
public class ElasticSearchController {
    @Autowired
    private SearchApi searchApi;

    @RequestMapping("/create")
    public String create() {
        try {
            searchApi.creatIndex();
            return "ok";
        } catch (IOException e) {
            e.printStackTrace();
            return "no";
        }
    }
}
