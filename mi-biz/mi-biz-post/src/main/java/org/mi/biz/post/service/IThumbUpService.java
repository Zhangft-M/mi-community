package org.mi.biz.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.post.entity.ThumbUp;
import org.mi.api.post.vo.ThumbUpVO;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 18:16
 **/
public interface IThumbUpService extends IService<ThumbUp> {
    /**
     * 点赞操作
     * @param thumbUp /
     */
    void thumbUp(ThumbUp thumbUp);

    /**
     * 通过用户的id查询
     * @param userId
     * @return
     */
    ThumbUpVO listByUserId(Long userId);
}
