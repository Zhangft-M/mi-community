package org.mi.biz.post.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.ThumbUp;
import org.mi.api.post.vo.ThumbUpVO;
import org.mi.biz.post.mapper.ThumbUpMapper;
import org.mi.biz.post.service.IThumbUpService;
import org.mi.common.core.constant.ThumbUpConstant;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 18:18
 **/
@Service
@RequiredArgsConstructor
public class ThumbUpServiceImpl extends ServiceImpl<ThumbUpMapper, ThumbUp> implements IThumbUpService {

    private final RedisUtils redisUtils;

    @Override
    public void thumbUp(ThumbUp thumbUp) {
        Integer type = thumbUp.getType();
        AssertUtil.notNull(thumbUp);
        if (type == 1 || type == -1) {
            if (type.equals(-1)) {
                // 记录点赞的用户id以及对点赞内容的id
                this.redisUtils.lRemove(ThumbUpConstant.USER_CONTENT_PREFIX + thumbUp.getUserId(),
                        1, thumbUp.getContentId());
            } else {
                this.redisUtils.lSet(ThumbUpConstant.USER_CONTENT_PREFIX + thumbUp.getUserId(), thumbUp.getContentId());
            }
            // 在redis中将对应的内容的点赞的值加1或者减1
            this.redisUtils.incrementValue(ThumbUpConstant.CONTENT_THUMB_UP_NUM_PREFIX + thumbUp.getContentId(), type.longValue());
            return;
        }
        throw new IllegalParameterException("传递的参数应该为-1或者为1");
    }

    @Override
    public ThumbUpVO listByUserId(Long userId) {
        List<Object> list = this.redisUtils.lGet(ThumbUpConstant.USER_CONTENT_PREFIX, 0, -1);
        ThumbUpVO thumbUpVO = new ThumbUpVO();
        List<Long> results = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            results.addAll(list.stream().map(data -> (Long) data).collect(Collectors.toList()));
        } else {
            List<ThumbUp> thumbUps = this.baseMapper.selectList(Wrappers.<ThumbUp>lambdaQuery().eq(ThumbUp::getUserId, userId));
            Optional.ofNullable(thumbUps).ifPresent(datas -> {
                results.addAll(datas.stream().map(ThumbUp::getContentId).collect(Collectors.toList()));
                this.redisUtils.lSet(ThumbUpConstant.USER_CONTENT_PREFIX + userId, results);
            });
        }
        thumbUpVO.setUserId(userId);
        thumbUpVO.setContentId(results);
        return thumbUpVO;
    }
}
