package org.mi.biz.tool.service.impl;

import lombok.RequiredArgsConstructor;
import org.mi.api.tool.entity.Checker;
import org.mi.biz.tool.service.IContentCheckService;
import org.mi.biz.tool.util.ContentVerifyHelper;
import org.springframework.stereotype.Service;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 22:16
 **/
@Service
@RequiredArgsConstructor
public class ContentCheckServiceImpl implements IContentCheckService {

    private final ContentVerifyHelper contentVerifyHelper;

    @Override
    public Checker checkTxt(String content, Long userId) {
        return this.contentVerifyHelper.checkTxtContent(content,userId);
    }

    @Override
    public Checker checkPic(String url, Long userId) {
        return this.contentVerifyHelper.checkImageContent(url,userId);
    }
}
