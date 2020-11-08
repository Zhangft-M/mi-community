package org.mi.api.post.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 16:36
 **/
@Data
@Document(indexName = "mi-community")
public class EsPostEntity {

    @Id
    @Field(value = "_id")
    private Long id;

    @Field(index = false)
    private Long categoryId;

    @Field(index = false)
    private Long userId;

    private String username;

    private String title;

    @Field(index = false)
    private Integer voteUp;

    @Field(index = false)
    private Integer voteDown;

    @Field(index = false)
    private Integer recommend;

    @Field(index = false)
    private Integer commentCount;

    @Field(index = false)
    private Integer level;

    @Field(index = false)
    private Integer status;

    @Field(index = false)
    private Integer viewCount;

    @Field(index = false)
    private Integer reward;

    @Field(index = false)
    private Integer delete;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
