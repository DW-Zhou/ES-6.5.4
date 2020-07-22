package com.dengzhou.sms;

import com.dengzhou.utils.ESClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.Map;

public class create_sms_index {
    String index = "sms_logs_index";
    String type = "sms_logs_type";

    @Test
    public void create_index() throws IOException {
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);

        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("createDate")
                            .field("type", "date")
                            .field("format","yyyy-MM-dd")
                        .endObject()
                        .startObject("sendDate")
                            .field("type", "date")
                            .field("format", "yyyy-MM-dd")
                        .endObject()
                        .startObject("longCode")
                            .field("type", "keyword")
                        .endObject()
                        .startObject("mobile")
                            .field("type", "keyword")
                        .endObject()
                        .startObject("corpName")
                            .field("type", "keyword")
                        .endObject()
                        .startObject("smsContent")
                            .field("type", "text")
                            .field("analyzer", "ik_max_word")
                        .endObject()
                        .startObject("state")
                            .field("type", "integer")
                        .endObject()
                        .startObject("operatorid")
                            .field("type", "integer")
                        .endObject()
                        .startObject("province")
                            .field("type", "keyword")
                        .endObject()
                        .startObject("ipAddr")
                            .field("type", "ip")
                        .endObject()
                        .startObject("replyTotal")
                            .field("type", "integer")
                        .endObject()
                        .startObject("fee")
                            .field("type", "long")
                        .endObject()
                    .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type,mappings);

        RestHighLevelClient client = ESClient.getClient();
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    @Test
    public void exists() throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);

        RestHighLevelClient client = ESClient.getClient();
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    public void testQuery() throws IOException {
//        1 创建Request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);
//        2 指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.termQuery("province", "河北"));

        request.source(builder);
//        3 执行查询
        RestHighLevelClient client = ESClient.getClient();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//        4  获取到_source中的数据
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }
}
