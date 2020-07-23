package com.dengzhou.sms;

import com.dengzhou.utils.ESClient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class test_sms_search {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    @Test
    public void test_get_id() throws IOException {
        GetRequest request = new GetRequest(index,type,"1");
        RestHighLevelClient client = ESClient.getClient();
        GetResponse resp = client.get(request, RequestOptions.DEFAULT);
        System.out.println(resp.getSourceAsMap());

    }

    @Test
    public void test_query_ids() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("1","2","3"));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void test_query_prefix() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery("smsContext","河"));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void test_query_fuzzy() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("corpName","盒马生鲜").prefixLength(2));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void test_query_wildcard() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
       builder.query(QueryBuilders.wildcardQuery("corpName","*车"));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }
    @Test
    public void test_query_range() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.rangeQuery("fee").lt(5).gt(2));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void test_query_regexp() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.regexpQuery("moible","106[0-9]{8}"));
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void test_query_scroll() throws IOException {
//        1   创建SearchRequest
        SearchRequest request = new SearchRequest(index);
        request.types(type);
//        2   指定scroll信息,生存时间
        request.scroll(TimeValue.timeValueMinutes(1L));
//        3   指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(2);
        builder.sort("fee",SortOrder.DESC);
        builder.query(QueryBuilders.matchAllQuery());
//        4 获取返回结果scrollid ,source
        request.source(builder);
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request,RequestOptions.DEFAULT);
        String scrollId = response.getScrollId();
        System.out.println(scrollId);
        while(true){
//       5  循环创建SearchScrollRequest
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        // 6 指定scrollid生存时间
        scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
//        7 执行查询获取返回结果
        SearchResponse scrollResp = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//        8.判断是否得到数据，输出
           if (scrollResp.getHits().getHits() != null && scrollResp.getHits().getHits().length > 0){
               System.out.println("=======下一页的数据========");
               for (SearchHit hit : scrollResp.getHits().getHits()){
                   System.out.println(hit.getSourceAsMap());
               }
           }else{
               //        9。判断没有查询到数据-退出循环
               System.out.println("没得");
               break;
           }
        }
        // 10  创建clearScrollRequest
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        // 11 指定scrollid
        clearScrollRequest.addScrollId(scrollId);
        // 12  删除
        client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
    }
}
