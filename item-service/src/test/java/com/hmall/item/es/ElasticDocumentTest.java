package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;



@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticDocumentTest {

    private RestHighLevelClient client;

    @Autowired
    private IItemService itemService;

    @Test
    void createIndexDoc() throws IOException {
        //0.准备文档数据
        //通过id查询数据库数据
        Item item = itemService.getById(1);
        //将数据库数据转化为文档数据
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);

        //1.准备request
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
        //2.准备请求参数
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
        //3.发送请求
        client.index(request, RequestOptions.DEFAULT);

    }

    @BeforeEach
    void setUp(){
        client= new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.6.132:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client != null){
            client.close();
        }
    }

}
