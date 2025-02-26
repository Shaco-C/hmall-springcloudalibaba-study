package com.hmall.item.es;


import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;


import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;


//@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticSearchTest {

    private RestHighLevelClient client;


    @Test
    void testSearchMatchAll() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("item");
        //2.配置request参数
        request.source()
                .query(QueryBuilders.matchAllQuery());
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        System.out.println(response);

        parseResponsResult(response);
    }

    //lte = less than equal <=
    //lt = less than <
    @Test
    void testSearch() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("item");
        //2.组织dsl参数
        request.source().query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name", "华为手机"))
                        .filter(QueryBuilders.termQuery("brand","华为"))
                        .filter(QueryBuilders.rangeQuery("price").gte(1000).lt(5000))
        );
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        System.out.println(response);

        parseResponsResult(response);
    }

    //sort 排序
    //from 从第几条元素开始查询
    //size 显示多少条数据

    @Test
    void testSortAndPage() throws IOException {
        int pageNo = 0;
        int pageSize = 500;
        //1.创建request对象
        SearchRequest request = new SearchRequest("item");
        //2.组织dsl参数
        request.source().query(
                QueryBuilders.matchAllQuery()
        ).sort("price", SortOrder.DESC).from((pageNo-1)*pageSize ).size(pageSize);
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponsResult(response);
    }

    @Test
    void testHighlight() throws IOException {

        //1.创建request对象
        SearchRequest request = new SearchRequest("item");
        //2.组织dsl参数
        request.source().query(
                QueryBuilders.matchQuery("name","牛奶")
        );
        //高亮
        request.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("name")
                        .preTags("<em>")
                        .postTags("</em>")
        );
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponsResult(response);
    }

    private static void parseResponsResult(SearchResponse response) {
        //4.解析查询结果，后续好返回给前端
        SearchHits hits = response.getHits();
        //获取总条数
        long total = hits.getTotalHits().value;
        System.out.println("总条数：" + total);
        //获取hits中的数据
        SearchHit[] searchHits = hits.getHits();

        for (SearchHit searchHit : searchHits) {
            String json = searchHit.getSourceAsString();
            System.out.println(json);
            ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);

            //处理高亮结果
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (highlightFields !=null && !highlightFields.isEmpty()){
                //根据高亮字段名获取字段
                HighlightField name = highlightFields.get("name");
                //获取高亮结果,覆盖非高亮结果
                String hfname = name.getFragments()[0].toString();
                itemDoc.setName(hfname);
            }

            System.out.println(itemDoc);
        }
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
