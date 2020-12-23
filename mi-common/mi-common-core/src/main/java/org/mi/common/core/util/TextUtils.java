package org.mi.common.core.util;

import org.springframework.data.repository.util.TxUtils;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-21 23:08
 **/
public class TextUtils {

    private static final int MAX_LENGTH = 30;

    private static final String REPLACE_STR = "......";

    /**
     * 隐藏过多的文本内容
     * @param text /
     */
    public static String hideText(String text){
        if (text.length() > MAX_LENGTH) {
            String substring = text.substring(0, MAX_LENGTH);
            return substring.concat(REPLACE_STR);
        }
        return text;
    }
}
