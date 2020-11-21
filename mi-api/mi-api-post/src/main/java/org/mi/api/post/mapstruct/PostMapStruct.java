package org.mi.api.post.mapstruct;

import org.common.mp.component.BaseMapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mi.api.post.dto.PostDTO;
import org.mi.api.post.entity.Post;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 17:08
 **/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapStruct extends BaseMapStruct<PostDTO, Post> {
}
