package org.mi.api.post.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 18:23
 **/
@Data
@Document(indexName = "mi-comment")
public class EsComment implements Serializable {

    private static final long serialVersionUID = -647720574304540351L;

    @Id
    @Field(value = "_id")
    private Long id;

    @Field(value = "parent_id")
    private Long parentId;

    @Field(value = "post_id")
    private Long postId;

    @Field(value = "user_id")
    private Long userId;

    private String username;

    @Field(value = "user_avatar")
    private String userAvatar;

    private String content;

    @Field(value = "vote_up")
    private Integer voteUp;

    @Field(value = "vote_down")
    private Integer voteDown;

    @Field(value = "has_adoption")
    private Boolean hasAdoption;

    private Boolean status;

    @Field(value = "has_delete")
    private Boolean hasDelete;

    @Field(value = "create_time")
    private LocalDateTime createTime;

    @Field(value = "update_time")
    private LocalDateTime updateTime;
}
