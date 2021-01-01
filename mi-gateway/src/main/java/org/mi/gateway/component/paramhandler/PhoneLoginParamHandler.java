package org.mi.gateway.component.paramhandler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.google.common.collect.Maps;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.mi.gateway.config.WebClientConfigProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 18:29
 **/
@Component("phoneLoginParamHandler")
public class PhoneLoginParamHandler extends AbstractParamHandler {

    private final WebClientConfigProperties clientConfigProperties;

    protected PhoneLoginParamHandler(RSA rsa, WebClientConfigProperties clientConfigProperties) {
        super(rsa);
        this.clientConfigProperties = clientConfigProperties;
    }

    @Override
    public Map<String, Object> dealAndPackageParams(Map<String, Object> attributes) {
        String phoneNumber = String.valueOf(attributes.get(MiUserConstant.PHONE_NUMBER));
        AssertUtil.notBlank(phoneNumber);
        Map<String,Object> params = Maps.newConcurrentMap();
        params.put(StrUtil.toCamelCase(AuthClientConstant.CLIENT_ID), this.clientConfigProperties.getClientId());
        params.put(StrUtil.toCamelCase(AuthClientConstant.CLIENT_SECRET), this.clientConfigProperties.getClientSecret());
        params.put(StrUtil.toCamelCase(AuthClientConstant.GRANT_TYPE), this.clientConfigProperties.getPhoneVerifyCodeGrantType());
        params.put(AuthClientConstant.SCOPE, this.clientConfigProperties.getScope());
        params.put(MiUserConstant.PHONE_NUMBER,phoneNumber);
        return params;
    }
}
