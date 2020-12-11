package org.mi.api.tool.api;

import com.baomidou.mybatisplus.extension.api.R;
import org.mi.api.tool.api.fallback.contentCheckRemoteApiFallback;
import org.mi.api.tool.entity.Checker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-11 22:55
 **/
@FeignClient(name = "MI-TOOL-SERVER",contextId = "contentCheckRemoteApi",fallback = contentCheckRemoteApiFallback.class)
public interface ContentCheckRemoteApi {

    /**
     * 文本检验
     * @param content
     * @return
     */
    @GetMapping("/content/check/txt")
    R<Checker> checkTxt(String content);

    /**
     * 图片检验
     * @param url
     * @return
     */
    @GetMapping("/content/check/pic")
    R<Checker> checkPic(String url);
}
