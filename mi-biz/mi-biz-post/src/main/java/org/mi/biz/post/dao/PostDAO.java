package org.mi.biz.post.dao;

import org.elasticsearch.repositories.Repository;
import org.mi.api.post.entity.EsPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 18:05
 **/
public interface PostDAO extends ElasticsearchRepository<EsPostEntity,Long> {

    Page<EsPostEntity> findByCategoryIdOrTitleOrUsername(Long categoryId, String title, String username, Pageable pageable);
}
