package org.mi.biz.tool.service;

import org.mi.api.tool.entity.Checker;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 17:49
 **/
public interface IContentCheckService {

    /**
     * 检验文本
     * @param content
     * @param userId
     * @return
     */
    Checker checkTxt(String content, Long userId);

    /**
     * 检验图片
     * @param url
     * @param userId
     * @return
     */
    Checker checkPicFromUrl(String url, Long userId);

    /**
     * 对文本进行审核校验，不带用户名id，不进行二次审核
     * @param content
     * @return
     */
    Checker checkTxtWithoutUserId(String content);
}
