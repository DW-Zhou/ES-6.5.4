package com.dengzhou.test;

import com.dengzhou.utils.ESClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

public class Demo1 {

    @Test
    public void testConnect(){
        RestHighLevelClient client = ESClient.getClient();
        System.out.println(client);
    }

}
