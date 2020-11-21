package org.mi.post.test;

import java.lang.reflect.Field;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-15 13:02
 **/
public class CommonTest {

    private static Boolean a = true;

    public static void main(String[] args) throws NoSuchFieldException {
        Class<CommonTest> commonTestClass = CommonTest.class;
        Field a = commonTestClass.getDeclaredField("a");
        System.out.println();
        System.out.println(a.getType().equals(Boolean.class));
    }
}
