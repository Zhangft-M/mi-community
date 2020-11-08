package org.mi.api.post.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 17:32
 **/
@Data
public class PostDTO implements Serializable {

    private static final long serialVersionUID = -2954249342145882967L;

    private Long id;

    private Long categoryId;

    private Long userId;

    private String title;

    private String username;

    private Integer viewCount;
}
