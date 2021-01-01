package org.mi.gateway.test;

import org.junit.jupiter.api.Test;
import org.mi.common.core.constant.SecurityConstant;
import org.mi.gateway.model.enu.ParamHandlerEnum;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-27 17:30
 **/
public class EnumTest {

    @Test
    public void enumTest(){
        System.out.println(ParamHandlerEnum.geBeanName(SecurityConstant.PASSWORD_CHANGE_PATH));

    }
}
