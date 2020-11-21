package org.mi.api.post.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 17:04
 **/
@Data
public class CommentTree implements Serializable {
    private static final long serialVersionUID = 3321210327682856089L;

    private Long id;

    private Long parentId;

    private Long userId;

    private String content;

    private Integer voteUp;

    private Integer voteDown;

    private String username;

    private String parentName;

    private String userAvatar;

    private Boolean hasAdoption;

    private LocalDateTime updateTime;

    private List<CommentTree> children;

    public void add(CommentTree treeNode){
        this.children.add(treeNode);
    }
}
