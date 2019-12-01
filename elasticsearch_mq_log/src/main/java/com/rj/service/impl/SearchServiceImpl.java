package com.rj.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.service.SearchApi;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("searchService")
public class SearchServiceImpl implements SearchApi {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestHighLevelClient client;

    @Value("${elasticsearch.index.name}") //库名，会直接从application.properties中查找
    private String indexName;

    @Value("${elasticsearch.type.name}")
    private String typeName;

    @Override
    public void creatIndex(){


    }

    @Override
    public boolean existIndex(String indexName) {
        GetIndexRequest request = new GetIndexRequest();



        return false;
    }
}
