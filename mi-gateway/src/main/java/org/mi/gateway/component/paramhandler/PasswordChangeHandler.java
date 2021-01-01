package org.mi.gateway.component.paramhandler;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 16:51
 **/
@Slf4j
@Component("passwordChangeHandler")
public class PasswordChangeHandler extends AbstractParamHandler {
    protected PasswordChangeHandler(RSA rsa) {
        super(rsa);
    }

    @Override
    public Map<String, Object> dealAndPackageParams(Map<String, Object> attributes) {
        try {
            String encodePassword = String.valueOf(attributes.get(MiUserConstant.PASSWORD));
            Map<String,Object> packageParams = Maps.newConcurrentMap();
            String decodePassword = this.rsa.decryptStr(encodePassword, KeyType.PrivateKey, StandardCharsets.UTF_8);
            AssertUtil.notBlank(decodePassword);
            packageParams.put(MiUserConstant.PASSWORD,decodePassword);
            return packageParams;
        } catch (Exception e) {
            log.warn("修改密码:解密失败ex=>{}",e.toString());
            throw new IllegalRequestException("非法请求");
        }

    }
}
