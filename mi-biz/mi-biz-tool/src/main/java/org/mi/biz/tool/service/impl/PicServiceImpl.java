package org.mi.biz.tool.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.RequiredArgsConstructor;
import org.mi.api.tool.entity.Checker;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiUser;
import org.mi.biz.tool.config.OssConfig;
import org.mi.biz.tool.service.IPicService;
import org.mi.biz.tool.util.ContentVerifyHelper;
import org.mi.common.core.exception.ContentNotSaveException;
import org.mi.common.core.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 14:50
 **/
@Service
@RequiredArgsConstructor
public class PicServiceImpl implements IPicService {

    private final OSS ossClient;

    private final OssConfig ossConfig;

    private final ContentVerifyHelper contentVerifyHelper;

    private final MiUserRemoteApi userRemoteApi;

    @Override
    public String uploadAvatarPic(MultipartFile multipartFile, Long userId) {
        try {
            PutObjectResult result = this.ossClient.putObject(this.ossConfig.getBucket().get("avatar"),
                    FileUtils.createFilePath(multipartFile, userId),
                    new ByteArrayInputStream(multipartFile.getBytes()));
            // result.getETag()
            if (result.getResponse().isSuccessful()) {
                String uri = result.getResponse().getUri();
                // 检验是否违法
                Checker checker = this.contentVerifyHelper.checkImageContent(uri, userId);
                if (checker.getStatus()) {
                    // TODO: 2020/12/11 检验通过,调用用户微服务修改用户的信息
                    MiUser miUser = new MiUser();
                    miUser.setAvatar(uri);
                    this.userRemoteApi.updateUserInfo(miUser);
                    // 返回url
                    return uri;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }
        return null;
    }
}
