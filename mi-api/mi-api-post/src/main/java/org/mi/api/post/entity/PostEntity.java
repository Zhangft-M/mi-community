package org.mi.api.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.common.mp.component.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-08 15:29
 **/
@Data
@TableName("mi_post")
@EqualsAndHashCode(callSuper = true)
public class PostEntity extends BaseEntity<PostEntity> {

    private static final long serialVersionUID = -6880346309325408139L;

    @TableId(type = IdType.ASSIGN_ID)
    @Null(message = "添加时不能指定ID")
    private Long id;

    @NotNull(message = "必须有类别")
    private Long categoryId;

    @NotNull(message = "必须有添加人")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "类容不能为空")
    private String content;

    private Integer voteUp;

    private Integer voteDown;

    private Integer recommend;

    private Integer commentCount;

    private Integer level;

    private Boolean status;

    private Integer viewCount;

    private Integer reward;



}
