package org.mi.api.post.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 16:36
 **/
@Data
@Document(indexName = "mi_post")
public class EsPost implements Serializable {

    private static final long serialVersionUID = 827005331631540310L;

    @Id
    @Field(value = "_id",docValues = true)
    private Long id;

    @Field(value = "category_id")
    private Long categoryId;

    @Field(value = "user_id")
    private Long userId;

    @Field(value = "user_nick_name",analyzer = "ik_max_word")
    private String userNickName;

    @Field(value = "user_avatar")
    private String userAvatar;

    @HighlightField
    @Field(analyzer = "ik_max_word")
    private String title;

    @HighlightField
    @Field(analyzer = "ik_max_word")
    private String content;

    @Field(value = "vote_up")
    private Integer voteUp;

    @Field(value = "vote_down")
    private Integer voteDown;

    private Boolean recommend;

    @Field(value = "comment_count")
    private Integer commentCount;

    private Boolean top;

    private Boolean status;

    @Field(value = "view_count")
    private Long viewCount;

    private Integer point;

    @Field(value = "has_delete")
    private Boolean delete;

    /**
     * 是否为精华帖子
     */
    private Boolean essence;

    /**
     * 是否完结
     */
    private Boolean ending;

    @Field(value = "create_time",format = DateFormat.basic_date_time)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Field(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
