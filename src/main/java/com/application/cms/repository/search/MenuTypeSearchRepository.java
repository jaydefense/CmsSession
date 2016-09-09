package com.application.cms.repository.search;

import com.application.cms.domain.MenuType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the MenuType entity.
 */
public interface MenuTypeSearchRepository extends ElasticsearchRepository<MenuType, Long> {
}
