package org.mi.biz.tool.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.tool.dto.PictureDTO;
import org.mi.biz.tool.service.IPicService;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.annotation.Anonymous;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 14:07
 **/
@RestController
@RequestMapping("pic")
@RequiredArgsConstructor
public class PicController {

    private final IPicService picService;

    @PostMapping("upload")
    public R<String> uploadAvatarPic(@RequestParam("avatar") MultipartFile multipartFile){
        AssertUtil.isPicture(multipartFile);
        Long userId = SecurityContextHelper.getUserId();
        return R.success(this.picService.uploadAvatarPic(multipartFile,userId));
    }


    @PostMapping("/upload/base64")
    public R<String> uploadPostPic(@RequestParam("postImage") MultipartFile multipartFile){
        AssertUtil.isPicture(multipartFile);
        Long userId = SecurityContextHelper.getUserId();
        String url = this.picService.uploadPostPic(multipartFile,userId);
        return R.success(url);
    }


    @GetMapping
    public R<Void> exceptionTest(){
        throw new IllegalParameterException("参数不合法");
    }
}
