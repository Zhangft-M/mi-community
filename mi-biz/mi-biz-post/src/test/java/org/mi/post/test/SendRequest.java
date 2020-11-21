package org.mi.post.test;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mi.biz.post.MiPostApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-17 12:44
 **/
public class SendRequest {


    @Test
    public void sendRequest(){
        /*HttpRequest request = new HttpRequest("http://h5.6f6364453835774f636a364e4254676f517559375735316937316a6f3a73.xingya.xiehuangbao.top/app/index.php");
        request.header(Header.REFERER,"");
        request.execute()*/
        String url = "http://h5.6f6364453835774f636a364e4254676f517559375735316937316a6f3a73.xingya.xiehuangbao.top/app/index.php" + "?";
        Map<String,Object> map = new HashMap<>();
        map.put("c","entry");
        map.put("do","vote");
        map.put("m","xiaof_toupiao");
        map.put("i",2);
        map.put("type","good");
        map.put("id",15296);
        /*Map<String,Object> map2 = new HashMap<>();
        map2.put("c","entry");
        map2.put("do","vote");
        map2.put("m","xiaof_toupiao");
        map2.put("i",2);*/
        map.put("wxref","mp.weixin.qq.com");
        for (int i = 17700; i < 20000; i++) {
            map.put("sid",i);
           //  map2.put("sid",i);
            String query1 = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
            String refer = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
            HttpResponse execute = HttpUtil.createGet(url + query1).header(Header.REFERER, url + refer).header(Header.COOKIE,"PHPSESSID = 7fd5705a7daf73b5471528a7951e0d5a").execute();
            System.out.println("i = " + i + UnicodeUtil.toString(new String(execute.bodyBytes())));
        }
    }
}
