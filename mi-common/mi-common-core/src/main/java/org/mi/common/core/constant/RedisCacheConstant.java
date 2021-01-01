package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-10-22 16:23
 **/
public interface RedisCacheConstant {

    String TOKEN_PREFIX = "mi_oauth:token:";

    String CLIENT_DETAILS_KEY = "mi:oauth:client:";

    String VERIFY_CODE_PREFIX = "verify:code:";

    String USER_COLLECT_POST = "user:collect:id:";

    String USER_DETAILS_INFO_CACHE_PREFIX = "user:details:info:certificate:";

    String USER_INFO_CACHE_PREFIX = "user:info:id:";

    String POST_CATEGORIES_CACHE_PREFIX = "post:categories:";

    String USER_TODAY_HAS_LOGIN = "user:today:has:login:";

    String USER_OWN_POST_ID = "user:own:post:userId:";

    String POST_VIEW_COUNTS_PREFIX = "post:view:counts:id";

    String USER_POST_COUNT_CACHE_PREFIX = "user:post:count:id";

}
