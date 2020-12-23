package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.post.dto.EsPostDTO;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;
import org.mi.api.post.query.PostQueryCriteria;
import org.mi.api.post.vo.PostVO;
import org.mi.common.core.result.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:58
 **/
public interface IPostService extends IService<Post> {
    /**
     * 分页查询所有
     * @param criteria
     * @param pageParam
     * @return
     */
    PageResult list(PostQueryCriteria criteria, Pageable pageParam);

    /**
     * 发帖
     * @param postEntity
     */
    void savePost(Post postEntity);

    /**
     * 更新
     * @param postEntity
     */
    void updatePost(Post postEntity);

    /**
     * 批量删除
     * @param ids
     * @param userId
     */
    void deletePost(Set<Long> ids, Long userId);

    /**
     * 查询推荐的数据
     * @return
     */
    List<PostVO> listRecommend();

    EsPost getDataById(Long id);

    /**
     * 根据用户id查找用户最近发的十条记录
     * @param userId
     * @return
     */
    List<EsPostDTO> listByUserId(Long userId);

    /**
     * 查找用户收藏的
     * @param userId
     * @return
     */
    List<EsPostDTO> listUserFavorites(Long userId);

    /**
     * 根据用户的id删除用户发的帖子
     * @param userId /
     */
    void deletePostByUserId(Long userId);

}
