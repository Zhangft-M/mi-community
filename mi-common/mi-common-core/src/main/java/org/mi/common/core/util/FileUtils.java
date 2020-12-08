package org.mi.common.core.util;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-06 14:50
 **/
public class FileUtils {

    /**
     * 从类路径读取文件
     * @param path
     * @return
     */
    public static String readFileContent(String path){
        String s = "";
        ClassPathResource classPathResource = new ClassPathResource(path);
        BufferedReader reader = classPathResource.getReader(StandardCharsets.UTF_8);
        StringBuilder content = new StringBuilder();
        try {
            while (StrUtil.isNotBlank((s = reader.readLine()))){
                content.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }
}
