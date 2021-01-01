package org.mi.biz.tool.service.impl;


import com.alibaba.alicloud.context.oss.OssProperties;
import com.aliyun.oss.OSS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.tool.entity.Checker;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiUser;
import org.mi.biz.tool.config.OssConfig;
import org.mi.biz.tool.service.IPicService;
import org.mi.biz.tool.util.ContentVerifyHelper;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 14:50
 **/
@Slf4j
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
        String uri = uploadFile(multipartFile, "avatar", userId);
        // TODO: 2020/12/11 检验通过,调用用户微服务修改用户的信息
        /*MiUser miUser = new MiUser();
        miUser.setAvatar(uri);
        this.userRemoteApi.updateUserInfo(miUser,null);*/
        // 返回url
        return uri;
    }

    private String uploadFile(MultipartFile multipartFile, String bucketName, Long userId) {
        // 对内容进行检测,检测成功后再上传
        try {
            byte[] bytes1 = multipartFile.getBytes();
            this.contentVerifyHelper.tencentImageContentCheck(Base64Utils.encodeToString(bytes1), userId);
            String filePath = FileUtils.createFilePath(multipartFile, userId);
            log.info("开始上传图片");
            this.ossClient.putObject(this.ossConfig.getBucket().get(bucketName),
                    filePath, new ByteArrayInputStream(bytes1));
            log.info("上传成功");
            return HTTPS + this.ossConfig.getBucket().get(bucketName) + "." +
                    this.ossProperties.getEndpoint() + "/" + filePath;
        } catch (Exception e) {
            log.info("上传发生异常ex=>{}", e.toString());
            throw new RuntimeException(e);
        }

    }

    @Override
    public String uploadBase64Image(String image, Long userId) {
        Checker checker = this.contentVerifyHelper.tencentImageContentCheck(image, userId);
        if (checker.getStatus()) {
            byte[] bytes = Base64Utils.decodeFromString(image);
            String filePath = FileUtils.createFilePath("jpg", userId);
            this.ossClient.putObject(this.ossConfig.getBucket().get("post"),
                    filePath, new ByteArrayInputStream(bytes));
            return HTTPS + this.ossConfig.getBucket().get("post") + "." +
                    this.ossProperties.getEndpoint() + "/" + filePath;
        }
        return null;
    }

    @Override
    public String uploadPostPic(MultipartFile multipartFile, Long userId) {
        return this.uploadFile(multipartFile, "post", userId);
    }
}
