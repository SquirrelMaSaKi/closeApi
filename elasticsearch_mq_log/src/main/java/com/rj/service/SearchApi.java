package com.rj.service;

public interface SearchApi {
    //创建库表
    void creatIndex();

    //判断库是不是存在
    boolean existIndex(String indexName);
}
