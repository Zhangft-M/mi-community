package org.mi.gateway.component.paramhandler;

import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.gateway.component.abs.AbstractParamHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 16:30
 **/
@Slf4j
@Component("registerParamHandler")
public class RegisterParamHandler extends AbstractParamHandler {

    public RegisterParamHandler(RSA rsa) {
        super(rsa);
    }

    @Override
    public Map<String, Object> dealAndPackageParams(Map<String, Object> attributes) {
        JSON registerData = (JSON) attributes.get(MiUserConstant.REGISTER_PARAM);
        Map<String, Object> packageParams = this.decodeUsernameAndPassword(registerData);
        packageParams.put(MiUserConstant.PHONE_NUMBER,registerData.getByPath(MiUserConstant.PHONE_NUMBER));
        packageParams.put(MiUserConstant.NICK_NAME,registerData.getByPath(MiUserConstant.NICK_NAME));
        return packageParams;
    }
}
