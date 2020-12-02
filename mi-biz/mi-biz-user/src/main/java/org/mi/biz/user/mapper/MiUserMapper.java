package org.mi.biz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mi.api.user.entity.MiUser;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 19:01
 **/
public interface MiUserMapper extends BaseMapper<MiUser> {

    MiUser selectUserWithRoleByCertificate(@Param("certificate") String certificate, @Param("type")Integer type);
}
