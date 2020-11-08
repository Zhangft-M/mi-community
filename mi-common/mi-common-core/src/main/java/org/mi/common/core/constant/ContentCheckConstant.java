package org.mi.common.core.constant;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-08 18:00
 **/
public interface ContentCheckConstant {

    String CODE = "code";

    String DATA = "data";

    String RESULTS = "results";

    String SCENE = "scene";

    String SUGGESTION = "suggestion";
    /**
     * porn（鉴黄）、ad（图文违规识别）、terrorism（暴恐识别）
     */
    String[] SCENES = new String[]{"porn","ad","terrorism"};

    /**
     * 建议的处理方式
     */
    String PASS = "pass";

    String REVIEW = "review";

    String BLOCK = "block";
    // String[] SUGGESTIONS = new String[]{"pass","review","block"};
 }
