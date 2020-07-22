# ElaticSearch

## 1.索引基本操作

### 1.1 创建一个索引

```json
#创建一个索引
PUT /person
{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1
  }
}
```

### 1.2 查看索引信息

![image-20200716151737673](/var/folders/qm/chzcf3qj1vn5d3zlnd2652zc0000gn/T/abnerworks.Typora/image-20200716151737673.png)



```json
#查看索引
GET /person
```

### 1.3 删除索引

```json
#删除索引
DELETE /person
```



### 1.4 ES中Field可以指定的类型

![截屏2020-07-16下午3.45.31](/Users/zcy/Desktop/截屏2020-07-16下午3.45.31.png)

~~~json
#String:
	text：一般用于全文检索。将当前的field进行分词
# keyword: 当前的Field不可被分词 
#
#
#
#
~~~



### 1.5 创建索引并指定数据结构

——以创建小说为例子

~~~json
PUT /book
{
  "settings": {
      #备份数
    "number_of_replicas": 1,
      #分片数
   	"number_of_shards": 5
  },
    #指定数据结构
  "mappings": {
    #指定类型 Type
    "novel": {
    # 文件存储的Field属性名
      "properties": {
        "name": {
          "type": "text",
          "analyzer": "ik_max_word",
    #   指定当前的Field可以作为查询的条件
          "index": true
        },
        "authoor": {
          "type": "keyword"
        },
        "onsale": {
          "type": "date",
          "format": "yyyy-MM-dd"
        }
      }
    }
  }
}
~~~

### 1.6 文档的操作

- <u>文档在ES服务中的唯一标志，_index,   _type,    _id 三个内容为组合，来锁定一个文档，操作抑或是修改</u>

#### 1.6.1 新建文档

- 自动生成id

~~~Json
PUT /book/novel
{
  "name": "西游记",
  "authoor": "刘明",
  "onsale": "2020-12-11"
}
~~~

- **手动指定ID（更推荐）**

~~~json
PUT /book/novel/1
{
  "name": "三国演义",
  "authoor": "小明",
  "onsale": "2020-12-11"
}

~~~

#### 1.6.2 修改文档 

- <u>覆盖式修改</u>

  ~~~json
  POST /book/novel/1
  {
    "name": "三国演义",
    "authoor": "小明",
    "onsale": "2020-12-11"
  }
  
  
  ~~~

  

- <u>doc修改方式（更推荐）</u>

  ~~~json
  POST /book/novel/1/_update
  {
    "doc": {
      "name": "极品家丁"
    }
  }
  #先锁定文档，_update  修改需要的字段即可
  ~~~

#### 1.6.3  删除文档

- <u>删库跑路</u>

  ~~~json
  DELETE /book/novel/1
  ~~~

  

## 2. java操作ElaticSearch

### 2.1 Java链接ES

~~~Xml
1、创建Maven工程
	导入依赖
#  4个依赖
   1、1  elasticsearch
<!-- https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch -->
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>6.5.4</version>
</dependency>

   1、2  elasticsearch的高级API
<!-- https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client -->
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>6.5.4</version>
</dependency>

   1、3   junit
<!-- https://mvnrepository.com/artifact/junit/junit -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>

   1、4  lombok
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.12</version>
    <scope>provided</scope>
</dependency>
~~~

#### 2.1.2 创建测试类，连接ES

~~~java
// 先创建连接，工具类
public class ESClient {

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
~~~

### 2.2 java创建索引

~~~java
import com.dengzhou.utils.ESClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

~~~

### 2.3 检查索引是否存在，删除索引

~~~java
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
~~~

### 2.4 修改文档

- <u>添加文档操作</u>

  ~~~java
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
  ~~~

  

- <u>修改文档</u>

  ~~~java
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
  ~~~

  ### 2.5 删除文档

  

  ### 2.6 java批量操作文档

  

  ## 3. ElasticSearch练习

  <u>索引 ： sms-logs-index</u>

  <u>类型：sms-logs-type</u>

  | 字段名称   | 备注                                           |
  | ---------- | ---------------------------------------------- |
  | createDate | 创建时间String                                 |
  | sendDate   | 发送时间 date                                  |
  | longCode   | 发送长号码  如 16092389287811   string         |
  | Mobile     | 如 13000000000                                 |
  | corpName   | 发送公司名称，需要分词检索                     |
  | smsContent | 下发短信内容，需要分词检索                     |
  | State      | 短信下发状态 0 成功 1 失败      integer        |
  | Operatorid | 运营商编号1移动2联通3电信    integer           |
  | Province   | 省份                                           |
  | ipAddr     | 下发服务器IP地址                               |
  | replyTotal | 短信状态报告返回时长      integer              |
  | Fee        | 扣费                                   integer |
  |            |                                                |

  - 创建实例代码

    ~~~java
     public void create_index() throws IOException {
            Settings.Builder settings = Settings.builder()
                    .put("number_of_shards", 3)
                    .put("number_of_replicas", 1);
    
            XContentBuilder mappings = JsonXContent.contentBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("createDate")
                                .field("type", "text")
                            .endObject()
                            .startObject("sendDate")
                                .field("type", "date")
                                .field("format", "yyyy-MM-dd")
                            .endObject()
                            .startObject("longCode")
                                .field("type", "text")
                            .endObject()
                            .startObject("mobile")
                                .field("type", "text")
                            .endObject()
                            .startObject("corpName")
                                .field("type", "text")
                                .field("analyzer", "ik_max_word")
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
                                .field("type", "text")
                            .endObject()
                            .startObject("ipAddr")
                                .field("type", "text")
                            .endObject()
                            .startObject("replyTotal")
                                .field("type", "integer")
                            .endObject()
                            .startObject("fee")
                                .field("type", "integer")
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
    
    ~~~

    - <u>数据导入部分</u>

      ~~~json
      PUT /sms_logs_index/sms_logs_type/1
      {
        "corpName": "途虎养车",
        "createDate": "2020-1-22",
        "fee": 3,
        "ipAddr": "10.123.98.0",
        "longCode": 106900000009,
        "mobile": "1738989222222",
        "operatorid": 1,
        "province": "河北",
        "relyTotal": 10,
        "sendDate": "2020-2-22",
        "smsContext":   "【途虎养车】亲爱的灯先生，您的爱车已经购买",
        "state": 0
      }
      ~~~

      

  ## 4 . ElasticSearch的各种查询

  ### 4.1 term&terms查询

  #### 4.1.1 term查询

  - ​	<u>term的查询是代表完全匹配，搜索之前不会对你的关键字进行分词</u>

  ~~~json
  #term匹配查询
  POST /sms_logs_index/sms_logs_type/_search
  {
    "from": 0,   #limit  from,size
    "size": 5,
    "query": {
      "term": {
        "province": {
          "value": "河北"
        }
      }
    }
  }
  ##不会对term中所匹配的值进行分词查询
  ~~~

  

  ~~~java
  // java代码实现方式
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
  ~~~

  

