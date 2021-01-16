package org.mi.gateway.component.abs;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.constant.MiUserConstant;
import org.mi.common.core.exception.IllegalRequestException;
import org.mi.common.core.exception.util.AssertUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @program: mi-community
 * @description: 参数处理抽象类
 * @author: Micah
 * @create: 2020-12-27 15:41
 **/
@Slf4j
public abstract class AbstractParamHandler {

    protected final RSA rsa;

    protected AbstractParamHandler(RSA rsa) {
        this.rsa = rsa;
    }

    /**
     *
     * @param attributes 上一个过滤器存储的属性
     * @return /
     */
    public Map<String,Object> dealAndPackageParams(Map<String, Object> attributes){
        throw new RuntimeException("该方法需要子类实现");
    }

    protected Map<String,Object> decodeUsernameAndPassword(JSON param){
        try {
            Map<String,Object> packageParams = Maps.newConcurrentMap();
            String username = param.getByPath(MiUserConstant.USER_NAME, String.class);
            String password = param.getByPath(MiUserConstant.PASSWORD, String.class);
            String decodeUsername = this.rsa.decryptStr(username, KeyType.PrivateKey, StandardCharsets.UTF_8);
            String decodePassword = this.rsa.decryptStr(password, KeyType.PrivateKey, StandardCharsets.UTF_8);
            AssertUtil.notBlank(decodeUsername, decodePassword);
            packageParams.put(MiUserConstant.USER_NAME, decodeUsername);
            packageParams.put(MiUserConstant.PASSWORD, decodePassword);
            return packageParams;
        } catch (Exception e) {
            log.warn("参数解密失败");
            throw new IllegalRequestException("非法请求");
        }
    }
}
