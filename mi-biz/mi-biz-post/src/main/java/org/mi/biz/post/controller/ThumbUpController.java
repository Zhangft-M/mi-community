package org.mi.biz.post.controller;

import lombok.RequiredArgsConstructor;
import org.mi.api.post.entity.ThumbUp;
import org.mi.biz.post.service.IThumbUpService;
import org.mi.common.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-22 18:11
 **/
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/thumbUp")
public class ThumbUpController {

    private final IThumbUpService thumbUpService;

    @GetMapping("{userId}")
    public R<Set<Long>> listByUserId(@PathVariable Long userId){
        Set<Long> result = this.thumbUpService.listByUserId(userId);
        return R.success(result);
    }

    @PutMapping
    public R<Void> thumbUp(@RequestBody ThumbUp thumbUp){
        this.thumbUpService.thumbUp(thumbUp);
        return R.success();
    }
}
