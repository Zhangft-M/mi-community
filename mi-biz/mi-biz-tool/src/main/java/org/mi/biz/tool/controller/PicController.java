package org.mi.biz.tool.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.tool.dto.PictureDTO;
import org.mi.biz.tool.service.IPicService;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.common.core.result.R;
import org.mi.security.util.SecurityContextHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public R<String> uploadAvatarPic(MultipartFile multipartFile){
        AssertUtil.isAvatarPic(multipartFile);
        Long userId = SecurityContextHelper.getUserId();
        return R.success(this.picService.uploadAvatarPic(multipartFile,userId));
    }
}
