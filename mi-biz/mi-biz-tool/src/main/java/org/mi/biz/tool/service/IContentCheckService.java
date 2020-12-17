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
}
