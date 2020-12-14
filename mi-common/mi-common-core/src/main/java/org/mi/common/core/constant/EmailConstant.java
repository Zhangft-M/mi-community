package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:05
 **/
public interface EmailConstant {

    String EMAIL_TOPIC = "email";

    String EMAIL_POST_REPLY_TAG = "replyPost";

    String EMAIL_COMMENT_REPLY_TAG = "replyComment";

    String EMAIL_CODE_TAG = "code";

    String REPLY_POST_DESTINATION = EMAIL_TOPIC + ":" + EMAIL_POST_REPLY_TAG;

    String CODE_DESTINATION = EMAIL_TOPIC + ":" + EMAIL_CODE_TAG;

    String REPLY_COMMENT_DESTINATION = EMAIL_TOPIC + ":" + EMAIL_COMMENT_REPLY_TAG;
}
