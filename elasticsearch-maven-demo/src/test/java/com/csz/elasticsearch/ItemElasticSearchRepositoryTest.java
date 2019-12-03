package com.csz.elasticsearch;

import com.csz.domain.Item;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemElasticSearchRepositoryTest {

    @Autowired
    private ItemElasticSearchRepository elasticsearch;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createdd() {
        elasticsearchTemplate.createIndex(Item.class);
        elasticsearchTemplate.putMapping(Item.class);

    }


    @Test
    public void save() {
        Item item = new Item(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        elasticsearch.save(item);
    }

    @Test
    public void saveAll() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        elasticsearch.saveAll(list);
    }


    @Test
    public void find() {
       /* Iterable<Item> all = elasticsearch.findAll();
        all.forEach(System.out::println);*/

       /* Optional<Item> one = elasticsearch.findById(1l);
        Item item = one.get();
        System.out.println(item);*/

        elasticsearch.findAll(Sort.by(Sort.Order.desc("price"))).forEach(System.out::println);


    }

    @Test
    public void customFind() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //queryBuilder.withQuery(QueryBuilders.matchQuery("title", "坚果"));
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        Page<Item> search = elasticsearch.search(queryBuilder.build());
        List<Item> content = search.getContent();
        content.forEach(System.out::println);

    }

    @Test
    public void divideAndSortFind() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withPageable(PageRequest.of(1, 2));  //分页，1 为第二页，2为每页大小
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC)); //降序
        // queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        Page<Item> search = elasticsearch.search(queryBuilder.build());
        List<Item> content = search.getContent();
        content.forEach(System.out::println);

    }

    @Test
    public void aggFind() {
       NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
       queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        AggregatedPage<Item> search =(AggregatedPage<Item>) elasticsearch.search(queryBuilder.build());
        StringTerms brands =(StringTerms) search.getAggregation("brands");
        List<StringTerms.Bucket> buckets = brands.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getDocCount());
            System.out.println(bucket.getKeyAsString());
        });


    }


}
