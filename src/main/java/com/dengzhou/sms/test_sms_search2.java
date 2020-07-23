package com.dengzhou.sms;

import com.dengzhou.utils.ESClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class test_sms_search2 {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    @Test
    public void test_query_fuzzy() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.types(type);

        request.setQuery(QueryBuilders.rangeQuery("relyTotal").gt("2").lt("3"));

        RestHighLevelClient client = ESClient.getClient();
        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);

        System.out.println(response.toString());
    }
}