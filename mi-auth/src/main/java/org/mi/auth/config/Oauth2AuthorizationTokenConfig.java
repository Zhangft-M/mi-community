package org.mi.auth.config;

import lombok.RequiredArgsConstructor;
import org.mi.common.core.constant.RedisCacheConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.security.KeyPair;

/**
 * @program: mi-community
 * @description: token的相关配置类
 * @author: Micah
 * @create: 2020-10-24 22:44
 **/
@Configuration
@RequiredArgsConstructor
public class Oauth2AuthorizationTokenConfig {

    private final RedisConnectionFactory redisConnectionFactory;


    @Bean
    public TokenStore redisTokenStore(){
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisCacheConstant.TOKEN_PREFIX);
        return redisTokenStore;
    }

    /**
     * jwt 令牌 配置，非对称加密
     *JwtAccessTokenConverter类实现了{@link TokenEnhancer},在创建token的时候会调用{@link DefaultTokenServices#createAccessToken(OAuth2Authentication authentication) 方法}
     * 在该方法内部最后一段代码就判断是否存在TokenEnhancer的实例，如果存在则对token进行加强，最后生成的JWT实际是调用 {@link JwtAccessTokenConverter#enhance 方法 }
     * 在该方法中这段代码是关键代码 result.setValue(encode(result, authentication));
     * @return 转换器
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        final JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setKeyPair(keyPair());
        return accessTokenConverter;
    }

    /**
     * 密钥  keyPair.
     * 可用于生成 jwt / jwk.
     *
     * @return keyPair
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("oauth2.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("oauth2");
    }


    @Bean
    public TokenEnhancer customTokenEnhancer(){
        return new TokenEnhancer() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                authentication.getPrincipal();
                return token;
            }
        };
    }
}
