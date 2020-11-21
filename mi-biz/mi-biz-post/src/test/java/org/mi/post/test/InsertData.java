package org.mi.post.test;

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mi.api.post.entity.Comment;
import org.mi.api.post.entity.Post;
import org.mi.biz.post.MiPostApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-13 23:54
 **/
@SpringBootTest(classes = MiPostApplication.class)
@RequiredArgsConstructor
public class InsertData {

    String[] title = new String[]{"烦死啦", "可惜了", "然后勒"};

    String[] contents = new String[]{"那老夫斯卡哈苏联空军返回刻录机撒赖科技飞洒地方还很多反馈进度很快几乎回复联合设计覅耦合我iu了多少开发和离开HD和u但是反抗集散地哦加哦覅哦萨拉斯解放拉萨解放了喀什分厘卡回来爱丽丝的坚决打击灯笼裤洒家离开",
            "拉克丝房间里卡号发来安徽人返回该i是fish FIHFOI后悔哦i哦得分记录数据的立法和建设经费流口水两会上的回复框上都好亏发货宋康昊发动机欧式就是两款发动机十六客服的回家的路上开发商累了就看见都是垃圾了烧录卡辣椒酱",
            "疯狂垃圾ask龙华分局萨力克好伐啦离开对方加快了速度和fish佛i收到回复似乎四大佛教四大哈佛四大护法卡片反馈i怕是发卡普斯科夫就哦按实际发放紧哦碰建瓯文件打扫房间欧赛发单号i的话覅欧式几哦啊发泡剂跑就发破案骄傲冯绍峰建瓯价格哈开个会iohio"};

    Long[] postIds = new Long[]{526541970320142336L, 526541972513763328L, 526541972773810176L};

    Long[] parentIds = new Long[]{526923853394620416L, 526923855487578112L, 526923855726653440L,526923855969923072L,526923856213192704L};

    @Test
    public void insert() {
        for (int i = 0; i < 100; i++) {
            Post postEntity = new Post();
            postEntity.setTitle(title[i % 3]);
            postEntity.setUserId(1L);
            postEntity.setContent(contents[i % 3]);
            postEntity.setCategoryId((i % 6) + 1L);
            postEntity.setCommentCount(0);
            postEntity.setEnding(RandomUtil.randomBoolean());
            postEntity.setRecommend(RandomUtil.randomBoolean());
            postEntity.setHasDelete(RandomUtil.randomBoolean());
            postEntity.setTop(RandomUtil.randomBoolean());
            postEntity.setEssence(RandomUtil.randomBoolean());
            postEntity.setStatus(RandomUtil.randomBoolean());
            postEntity.insert();
        }

    }

    @Test
    public void InsertCommentData() {
        for (int i = 0; i < 2; i++) {
            Comment comment = Comment.builder()
                    .content(contents[i % 3])
                    .hasAdoption(false)
                    .postId(postIds[i % 3])
                    .userId((i % 2) + 1L)
                    .parentId(526924233369202688L)
                    .voteDown(0)
                    .voteUp(0)
                    .status(true).build();
            comment.setHasDelete(false);
            comment.insert();
        }
    }

}
