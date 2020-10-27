package org.mi.auth.service;

import lombok.SneakyThrows;
import org.mi.common.core.constant.AuthClientConstant;
import org.mi.common.core.constant.RedisCacheConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-24 23:17
 **/
@Service
public class CustomClientDetailsService extends JdbcClientDetailsService implements InitializingBean {
    public CustomClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 将查询结果进行缓存
     * @param clientId
     * @return
     */
    @Override
    @SneakyThrows
    @Cacheable(value = RedisCacheConstant.CLIENT_DETAILS_KEY, key = "#clientId", unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId) {
        return super.loadClientByClientId(clientId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setFindClientDetailsSql(AuthClientConstant.DEFAULT_FIND_STATEMENT);
        this.setSelectClientDetailsSql(AuthClientConstant.DEFAULT_SELECT_STATEMENT);
    }
}
