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

    /**
     * 更新用户的积分信息
     * @param oldPoint /
     * @param newPoint /
     * @param userId /
     */
    void updateUserPoint(Integer oldPoint, Integer newPoint, Long userId);

    /**
     * 修改密码
     * @param newPassword
     * @param userId
     */
    void changePassword(String newPassword, Long userId);

    /**
     * 注销用户,永久删除
     * @param userId /
     * @param phoneNumber /
     * @param verifyCode /
     */
    void deleteUser(Long userId, String phoneNumber, String verifyCode);

    /**
     * 更新用户信息
     * @param user
     * @param code
     * @return
     */
    MiUserDTO updateUserInfo(MiUser user, String code);

    /**
     * 增加用户的发帖数
     * @param userId
     */
    void incrementUserPostCount(Long userId);

    /**
     * 校验用户手机号是否正确
     * @param userId
     * @param phoneNumber
     * @param code
     */
    void checkUserPhoneNumber(Long userId, String phoneNumber, String code);
}
