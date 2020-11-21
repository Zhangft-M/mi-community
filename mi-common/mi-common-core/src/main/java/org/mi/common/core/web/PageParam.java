package org.mi.common.core.web;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: mi-community
 * @description: 分页参数
 * @author: Micah
 * @create: 2020-10-27 14:33
 **/
@Data
public class PageParam implements Serializable {

    private static final long serialVersionUID = -7854913742072115331L;

    private Long currentPage;

    private Long size;

    private String sort;
}
