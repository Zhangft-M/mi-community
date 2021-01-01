package org.mi.gateway.component.paramhandler;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSON;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.mi.gateway.config.WebClientConfigProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 16:00
 **/
@Slf4j
@Component("passwordLoginParamHandler")
public class PasswordLoginParamHandler extends AbstractParamHandler {

    private final WebClientConfigProperties clientConfigProperties;


    public PasswordLoginParamHandler(WebClientConfigProperties clientConfigProperties, RSA rsa) {
        super(rsa);
        this.clientConfigProperties = clientConfigProperties;
    }

    @Override
    public Map<String, Object> dealAndPackageParams(Map<String, Object> attributes) {
        JSON loginData = (JSON) attributes.get("loginData");
        Map<String, Object> packageParams = this.decodeUsernameAndPassword(loginData);
        packageParams.put(AuthClientConstant.CLIENT_ID, this.clientConfigProperties.getClientId());
        packageParams.put(AuthClientConstant.CLIENT_SECRET, this.clientConfigProperties.getClientSecret());
        packageParams.put(AuthClientConstant.GRANT_TYPE, this.clientConfigProperties.getPasswordGrantType());
        packageParams.put(AuthClientConstant.SCOPE, this.clientConfigProperties.getScope());
        return packageParams;
    }
}
