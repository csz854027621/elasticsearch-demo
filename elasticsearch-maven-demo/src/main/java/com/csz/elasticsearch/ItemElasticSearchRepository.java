package com.csz.elasticsearch;

import com.csz.domain.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemElasticSearchRepository extends ElasticsearchRepository<Item,Long> {



}
