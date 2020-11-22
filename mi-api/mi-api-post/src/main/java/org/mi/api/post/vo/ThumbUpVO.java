package org.mi.api.post.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 21:32
 **/
@Data
public class ThumbUpVO implements Serializable {

    private static final long serialVersionUID = 6128299151796966716L;

    private Long userId;

    private List<Long> contentId;
}
