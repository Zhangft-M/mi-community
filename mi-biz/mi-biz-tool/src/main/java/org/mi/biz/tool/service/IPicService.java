package org.mi.biz.tool.service;

import org.mi.api.tool.dto.PictureDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 14:47
 **/
public interface IPicService {
    String uploadAvatarPic(MultipartFile multipartFile, Long userId);

    /**
     * 上传base64编码格式的图片
     * @param image
     * @param userId
     * @return
     */
    @Deprecated
    String uploadBase64Image(String image, Long userId);

    /**
     * 上传帖子内容相关图片
     * @param multipartFile
     * @param userId
     * @return
     */
    String uploadPostPic(MultipartFile multipartFile, Long userId);
}
