package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.common.mp.component.PageParam;
import org.mi.api.post.entity.PostEntity;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.common.core.result.PageResult;

import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:58
 **/
public interface IPostService extends IService<PostEntity> {
    /**
     * 分页查询所有
     * @param criteria
     * @param pageParam
     * @return
     */
    PageResult list(PostQueryCriteria criteria, PageParam pageParam);

    /**
     * 发帖
     * @param postEntity
     */
    void savePost(PostEntity postEntity);

    /**
     * 更新
     * @param postEntity
     */
    void updatePost(PostEntity postEntity);

    /**
     * 批量删除
     * @param ids
     */
    void deletePost(Set<Long> ids);
}
