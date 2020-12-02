package org.mi.api.user.mapstruct;

import org.common.mp.component.BaseMapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-27 13:58
 **/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MiUserMapStruct extends BaseMapStruct<MiUserDTO, MiUser> {
}
