package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-29 17:21
 **/
public interface SmsMessageConstant {

    String VERIFY_CODE_TOPIC = "verify";

    String VERIFY_CODE_TAG = "code";

    String PHONE_CODE_DESTINATION = VERIFY_CODE_TOPIC + ":" + VERIFY_CODE_TAG;



}
