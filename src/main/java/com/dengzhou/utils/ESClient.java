package com.dengzhou.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public  class ESClient {

    public static RestHighLevelClient getClient(){
//        创建HttpHost对象
        HttpHost httpHost = new HttpHost("127.0.0.1",9200);
//      创建RestClientBuilder
        RestClientBuilder builder = RestClient.builder(httpHost);
        //      创建RestHighLevelClien对象
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }
}
