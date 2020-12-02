package org.mi.api.user.api.fallback;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.user.api.MiUserRemoteApi;
import org.mi.api.user.entity.MiUser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-30 17:05
 **/
@Slf4j
@Component
public class MiUserRemoteApiFallback implements MiUserRemoteApi {

    @Override
    public ResponseEntity<MiUser> loadUserByUsername(String certificate, Integer type, String from) {
        return null;
    }

    @Override
    public R<Void> updateLoginInfo(Map<String,Object> loginInfo, String from) {
        return null;
    }
}
