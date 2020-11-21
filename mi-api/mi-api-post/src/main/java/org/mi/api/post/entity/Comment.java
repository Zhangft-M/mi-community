package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:46
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@TableName("mi_comment")
public class Comment extends BaseEntity<Comment> {

    private static final long serialVersionUID = -7181206444596827629L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private Long postId;

    private Long userId;

    private String content;

    private Integer voteUp;

    private Integer voteDown;

    private Boolean hasAdoption;

    private Boolean status;
}
