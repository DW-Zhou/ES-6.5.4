package com.dengzhou.test;

import com.dengzhou.Person;
import com.dengzhou.utils.ESClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Create_ES_Index {
    String index = "person";
    String type = "man";
    @Test
    public void createIndex() throws IOException {
     //1、 准备关于索引的settings
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);


        //2、 准备关于索引的结构mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                            .field("type","text")
                        .endObject()
                        .startObject("age")
                            .field("type","integer")
                        .endObject()
                        .startObject("birthday")
                             .field("type","date")
                             .field("format","yyyy-MM-dd")
                        .endObject()
                    .endObject()
                .endObject();

        //2 将settings 和 mappings封装成一个request对象
        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type,mappings);
        //3   通过client对象去链接es并执行创建索引
        RestHighLevelClient client = ESClient.getClient();
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        //测试
        System.out.println("response"+response.toString());

    }

    //检查索引是否存在
    @Test
    public void exists() throws IOException {
        //1 准备request对象
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        // 2 通过client去检查
        RestHighLevelClient client = ESClient.getClient();
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 操作文档，添加数据
    @Test
    public void createDoc() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

//        1. 准备json数据
        Person person = new Person(1, "张三", 23, new Date());
        String json = mapper.writeValueAsString(person);
        System.out.println(json);

//        2. 准备一个request对象(手动指定id创建)
        IndexRequest indexRequest = new IndexRequest(index,type,person.getId().toString());
        indexRequest.source(json, XContentType.JSON);

//            3、通过client对象执行添加操作
        RestHighLevelClient client = ESClient.getClient();
        IndexResponse resp = client.index(indexRequest, RequestOptions.DEFAULT);

//            4、 输出返回
        System.out.println(resp.getResult().toString());
    }

//    修改文档，通过doc方式
    @Test
    public void updateDoc() throws IOException {
//        创建map,指定需要修改的内容
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name","李四");
        String docId = "1";
//           创建一个request对象，封装数据
        UpdateRequest updateRequest = new UpdateRequest(index,type,docId);
        updateRequest.doc(map);
//        通过client对象执行
        RestHighLevelClient client = ESClient.getClient();
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
//                返回输出结果
        System.out.println(update.getResult().toString());
    }

//    批量操作文档
    @Test
    public void bulkCreateDoc(){
        // 准备多个 json
        // 创建Request 将准备好的数据封装进去
        // 用Client执行
    }
}

