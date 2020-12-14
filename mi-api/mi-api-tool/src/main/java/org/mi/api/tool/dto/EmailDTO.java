package org.mi.api.tool.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:01
 **/
@Data
public class EmailDTO implements Serializable {

    private static final long serialVersionUID = 1014311867756847639L;

    /** 收件人，支持多个收件人 */
    @NotEmpty
    private String to;

    /**
     * 评论回复的用户
     */
    private String replyNickName;

    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 内容
     */
    @NotBlank
    private String content;
}
