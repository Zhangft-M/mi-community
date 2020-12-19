package org.mi.common.core.exception.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.exception.ContentNotSaveException;
import org.mi.common.core.exception.IllegalParameterException;
import org.mi.common.core.util.FileUtils;
import org.mi.common.core.util.PhoneUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Objects;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 00:23
 **/
@Slf4j
@UtilityClass
public class AssertUtil {

    private static final String EMAIL_FORMAT = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";

    public static void notNull(Object... o) {
        for (Object o1 : o) {
            notNull(o1, "参数为空不符合要求");
        }
    }

    public static void notNull(Object o, String str) {
        if (BeanUtil.isEmpty(o)) {
            log.warn("空指针异常");
            throw new NullPointerException(str);
        }
    }

    public static void notBlank(String str) {
        if (StrUtil.isBlank(str)) {
            log.warn("该字符串为空");
            throw new IllegalParameterException();
        }
    }

    public static void notBlank(String... str) {
        for (String s : str) {
            notBlank(s);
        }
    }

    public static void idIsNull(Long id) {
        if (!Objects.isNull(id)) {
            throw new IllegalParameterException(HttpStatus.BAD_REQUEST, "已经存在Id");
        }
    }

    public static void idsIsNull(Long... ids) {
        for (Long id : ids) {
            idIsNull(id);
        }
    }

    public static void idsIsNotNull(Long... ids) {
        for (Long id : ids) {
            idIsNotNull(id);
        }
    }

    public static void idIsNotNull(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalParameterException(HttpStatus.BAD_REQUEST, "没有id");
        }
    }

    public static <T> void collectionsIsNotNull(Collection<T> collection) {
        if (CollUtil.isEmpty(collection)) {
            throw new IllegalParameterException(HttpStatus.BAD_REQUEST, "集合为空,不符合要求");
        }
    }

    public static <T> void isPhoneNumber(String phoneNumber) {
        AssertUtil.notBlank(phoneNumber);
        try {
            if (!PhoneUtils.isPhoneLegal(phoneNumber)) {
                throw new IllegalParameterException("手机号格式不正确,仅支持大陆和香港手机号");
            }
        } catch (Exception e) {
            throw new IllegalParameterException("手机号格式不正确,仅支持大陆和香港手机号");
        }
    }

    public static void isAvatarPic(MultipartFile multipartFile){
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        if (!originalFilename.endsWith(".jpg")){
            throw new IllegalParameterException("图片只支持jpg格式");
        }
        if (!FileUtils.checkSize(1, multipartFile.getSize())){
            throw new IllegalParameterException("图片最大为1MB");
        }
    }

    public static void statusIsTrue(Boolean status,String msg){
        if (!status){
            throw new ContentNotSaveException(msg);
        }
    }

    public static void isEmail(String email){
        if (!email.matches(EMAIL_FORMAT)){
            throw new IllegalParameterException("邮箱格式不正确");
        }
    }
}
