package org.mi.biz.tool.controller;

import lombok.RequiredArgsConstructor;
import org.common.mp.component.BaseEntity;
import org.mi.api.tool.entity.Checker;
import org.mi.biz.tool.service.IContentCheckService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.mi.security.annotation.Inner;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mi-community
 * @description: 类容检验接口
 * @author: Micah
 * @create: 2020-12-11 17:43
 **/
@RestController
@RequestMapping("/content/check")
@RequiredArgsConstructor
public class ContentCheckController {

    private final IContentCheckService contentCheckService;



    @GetMapping("txt")
    public R<Checker> checkTxt(String content){
        Long userId = SecurityContextHelper.getUserId();
        Checker checker = this.contentCheckService.checkTxt(content,userId);
        return R.success(checker);
    }

    @GetMapping("pic")
    public R<Checker> checkPicFromUrl(String url){
        Long userId = SecurityContextHelper.getUserId();
        Checker checker = this.contentCheckService.checkPicFromUrl(url,userId);
        return R.success(checker);
    }

    @Inner
    @GetMapping("txt/withoutUser")
    public R<Checker> checkTxtWithoutUserId(String content){
        Checker checker = this.contentCheckService.checkTxtWithoutUserId(content);
        return R.success(checker);
    }
}
