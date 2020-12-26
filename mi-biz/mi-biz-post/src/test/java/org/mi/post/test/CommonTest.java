package org.mi.post.test;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-15 13:02
 **/
public class CommonTest {

    private static Boolean a = true;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(3,5,5000, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>()
            ,new CustomizableThreadFactory("post-deal-thread-"));

    public static void main(String[] args) throws NoSuchFieldException {

        System.out.println(StringEscapeUtils.escapeHtml("......</p>"));
    }

    @Test
    public void threadTest() throws InterruptedException {
        this.executor.execute(()->{
            for (int i = 0; i < 50; i++) {
                System.out.println("子线程打印" + i);
                if (i == 25) {
                    throw new RuntimeException("进程异常测试");
                }
            }
        });
        for (int i = 0; i < 100; i++) {
            System.out.println("主线程打印:" + i);
            Thread.sleep(1000);
        }
    }
}
