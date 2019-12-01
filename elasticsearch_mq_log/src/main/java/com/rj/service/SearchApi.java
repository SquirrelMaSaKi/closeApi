package com.rj.service;

import java.io.IOException;

public interface SearchApi {
    //创建库表
    void creatIndex() throws IOException;

    //判断库是不是存在
    boolean existIndex(String indexName) throws IOException;

    //添加数据
    void add(String json) throws IOException;
}
