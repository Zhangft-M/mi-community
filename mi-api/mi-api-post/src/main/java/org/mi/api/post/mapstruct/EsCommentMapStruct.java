package org.mi.api.post.mapstruct;

import org.common.mp.component.BaseMapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mi.api.post.entity.EsComment;
import org.mi.api.post.vo.CommentTree;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-19 19:06
 **/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EsCommentMapStruct extends BaseMapStruct<CommentTree, EsComment> {
}
