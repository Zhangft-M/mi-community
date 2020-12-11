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
}
