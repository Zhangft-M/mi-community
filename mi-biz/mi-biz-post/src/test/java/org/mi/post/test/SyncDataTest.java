package org.mi.post.test;

import org.junit.jupiter.api.Test;
import org.mi.biz.post.MiPostApplication;
import org.mi.biz.post.task.SyncThumbUpDataTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-24 12:21
 **/
@SpringBootTest(classes = MiPostApplication.class)
public class SyncDataTest {

    @Autowired
    private SyncThumbUpDataTask syncThumbUpDataTask;

    @Test
    public void syncData(){
        this.syncThumbUpDataTask.syncVoteUpData();
        this.syncThumbUpDataTask.syncThumbUpData();
    }
}