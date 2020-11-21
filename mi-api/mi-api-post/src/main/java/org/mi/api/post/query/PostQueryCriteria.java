package org.mi.api.post.query;

import lombok.Data;
import org.common.mp.annotation.Query;
import org.common.mp.annotation.type.SelectType;

import java.time.LocalDateTime;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:37
 **/
@Data
public class PostQueryCriteria  {

    private Long id;

    @Query(blurry = {"username","title","content"})
    private String keyword;

    @Query(value = "category_id")
    private Long categoryId;

    @Query
    private Boolean top;

    @Query
    private Boolean essence;

    @Query
    private Boolean ending;

}
