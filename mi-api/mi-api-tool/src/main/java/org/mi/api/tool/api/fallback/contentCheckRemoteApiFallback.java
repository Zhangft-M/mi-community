package org.mi.api.tool.api.fallback;

import com.baomidou.mybatisplus.extension.api.R;
import org.mi.api.tool.api.ContentCheckRemoteApi;
import org.mi.api.tool.entity.Checker;
import org.springframework.stereotype.Component;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 22:57
 **/
@Component
public class contentCheckRemoteApiFallback implements ContentCheckRemoteApi {

    @Override
    public Checker checkTxt(String content) {
        return null;
    }

    @Override
    public Checker checkPic(String url) {
        return null;
    }

    @Override
    public Checker checkTxtWithoutUserId(String content, String from) {
        return null;
    }
}
