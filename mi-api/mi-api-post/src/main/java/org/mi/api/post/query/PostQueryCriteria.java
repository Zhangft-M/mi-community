package org.mi.api.post.query;

import lombok.Data;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:37
 **/
@Data
public class PostQueryCriteria  {

    private Long id;

    private String title;

    private String username;
}
