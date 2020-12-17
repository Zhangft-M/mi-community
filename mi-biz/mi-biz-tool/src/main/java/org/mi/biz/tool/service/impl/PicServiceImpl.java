package org.mi.biz.tool.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.alicloud.context.oss.OssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.extension.api.R;
import io.netty.buffer.ByteBufInputStream;
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
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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

    private static final String HTTPS = "https://";

    private final OssProperties ossProperties;

    private final ContentVerifyHelper contentVerifyHelper;

    private final MiUserRemoteApi userRemoteApi;

    @Override
    public String uploadAvatarPic(MultipartFile multipartFile, Long userId) {

        try (FileInputStream is = new FileInputStream(FileUtils.toFile(multipartFile))) {
            // 对内容进行检测,检测成功后再上传
            this.contentVerifyHelper.tencentImageContentCheck(Base64Utils.encodeToString(multipartFile.getBytes()),userId);
            String filePath = FileUtils.createFilePath(multipartFile, userId);
            this.ossClient.putObject(this.ossConfig.getBucket().get("avatar"),
                    filePath, is);
            // result.getETag()
            String uri = HTTPS + this.ossConfig.getBucket().get("avatar") + "." +
                    this.ossProperties.getEndpoint() + "/" + filePath;
            // 检验是否违法
            // Checker checker = this.contentVerifyHelper.checkImageContent(uri, userId);
                // TODO: 2020/12/11 检验通过,调用用户微服务修改用户的信息
                MiUser miUser = new MiUser();
                miUser.setAvatar(uri);
                this.userRemoteApi.updateUserInfo(miUser);
                // 返回url
                return uri;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
