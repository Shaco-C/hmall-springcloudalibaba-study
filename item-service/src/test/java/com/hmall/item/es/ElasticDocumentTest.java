package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;


@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticDocumentTest {

    private RestHighLevelClient client;

    @Autowired
    private IItemService itemService;


    //是新增的方法，也是全量更新的方法
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

    @Test
    void testGetDoc() throws IOException {
        GetRequest getRequest = new GetRequest("items", "1");

        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

        String source = response.getSourceAsString();

        ItemDoc itemDoc = JSONUtil.toBean(source, ItemDoc.class);

        System.out.println(itemDoc);
    }

    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest getRequest = new DeleteRequest("items", "1");

        client.delete(getRequest, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("items","1");

        updateRequest.doc(
                "price",200,
                "name","测试商品"
        );

        client.update(updateRequest, RequestOptions.DEFAULT);
    }


    //将数据库中的信息写入到es中
    @Test
    void testBulk() throws IOException {

        int pageNo=1;
        int pageSize=500;

        while (true){

            //1.准备文档数据
            Page<Item> itemPage = itemService.lambdaQuery().eq(Item::getStatus, 1).page(Page.of(pageNo, pageSize));

            List<Item> items = itemPage.getRecords();

            if (items == null || items.isEmpty()){
                return;
            }

            BulkRequest bulkRequest = new BulkRequest();

//        bulkRequest.add(new IndexRequest("items").id("1").source("json", XContentType.JSON));
////        bulkRequest.add(new IndexRequest("items").id("1").source("json", XContentType.JSON));
////        bulkRequest.add(new IndexRequest("items").id("1").source("json", XContentType.JSON));
////        bulkRequest.add(new IndexRequest("items").id("1").source("json", XContentType.JSON));

            for (Item item : items) {
                bulkRequest.add(new IndexRequest("items")
                        .id(item.getId().toString())
                        .source(JSONUtil.toJsonStr(BeanUtil.copyProperties(item, ItemDoc.class)), XContentType.JSON));

            }
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            pageNo++;
        }

        //翻页
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
