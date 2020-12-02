package org.mi.biz.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mi.api.user.dto.MiUserDTO;
import org.mi.api.user.entity.MiUser;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-26 19:00
 **/
public interface IMiUserService extends IService<MiUser> {

    /**
     * 通过用户名加载用户
     * @param certificate 查询凭证
     * @param type 查询类型 0 为根据用户名查询，1为根据手机号查询
     * @return
     */
    MiUser loadUserByUsername(String certificate, Integer type);

    /**
     * 注册
     * @param miUser
     */
    void register(MiUser miUser);

    /**
     * 查询用户的信息
     * @param userId
     * @return
     */
    MiUserDTO getUserInfo(Long userId);
}
