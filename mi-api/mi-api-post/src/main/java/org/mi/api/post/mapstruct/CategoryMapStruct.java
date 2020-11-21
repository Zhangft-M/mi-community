package org.mi.api.post.mapstruct;

import org.common.mp.component.BaseMapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mi.api.post.dto.CategoryDTO;
import org.mi.api.post.entity.Category;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-16 17:29
 **/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapStruct extends BaseMapStruct<CategoryDTO, Category> {
}
