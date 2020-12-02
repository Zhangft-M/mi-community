package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.common.mp.component.BaseEntity;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:46
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mi_comment")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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
