package org.mi.api.post.mapstruct;

import org.common.mp.component.BaseMapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mi.api.post.dto.EsPostDTO;
import org.mi.api.post.entity.EsPost;
import org.mi.api.post.entity.Post;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 17:08
 **/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EsPostMapStruct extends BaseMapStruct<EsPostDTO, EsPost> {
}
