package org.mi.api.user.entity;

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
 * @create: 2020-12-21 19:40
 **/
@Data
@TableName("mi_user_own_post")
@EqualsAndHashCode(callSuper = true)
public class UserOwnPost extends BaseEntity<UserOwnPost> {

    private static final long serialVersionUID = 3262416012698492479L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long postId;

    private Integer usePoint;
}
