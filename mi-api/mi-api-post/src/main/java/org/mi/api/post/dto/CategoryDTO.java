package org.mi.api.post.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 16:13
 **/
@Data
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = -5577270358559025994L;

    private Long id;

    private String name;

    private String icon;

    private String iconClass;

    private Integer orderNum;
}
