package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-09 14:44
 **/
@Data
@TableName("mi_user_post_favorites")
@EqualsAndHashCode(callSuper = true)
public class UserPostFavorites extends BaseEntity<UserPostFavorites> {

    private static final long serialVersionUID = -1658434892263049530L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long postId;

    private Boolean hasDelete;
}
