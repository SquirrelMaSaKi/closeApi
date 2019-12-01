package com.rj.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.service.SearchApi;
import com.rj.utils.SearchUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    public void creatIndex() throws IOException {
        if (!existIndex(indexName)) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder().put("number_of_replicas", 1).put("number_of_shards", 5).build());
            SearchUtil.buildMapping(typeName, request);
            client.indices().create(request, RequestOptions.DEFAULT);
        } else {
            System.err.println("创建库失败");
        }
    }

    @Override
    public boolean existIndex(String indexName) throws IOException {
        //这里使用的是springboot2.2.1,所以对应的es也不一样，注意导包，与2.1.9有区别,必须是“action.admin.indices”的包
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);
        return client.indices().exists(request,RequestOptions.DEFAULT);
    }

    @Override
    public void add(String json) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName,typeName);
        indexRequest.source(json, XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
    }
}
