package org.mi.common.core.exception.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mi.common.core.exception.IllegalParameterException;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-11-07 00:23
 **/
@Slf4j
@UtilityClass
public class AssertUtil  {

    public static void notNull(Object... o){
        for (Object o1 : o) {
            notNull(o1,"参数为空不符合要求");
        }
    }

    public static void notNull(Object o,String str){
        if (BeanUtil.isEmpty(o)){
            log.warn("空指针异常");
            throw new NullPointerException(str);
        }
    }

    public static void notBlank(String str){
        if (StrUtil.isBlank(str)){
            log.warn("该字符串为空");
            throw new IllegalParameterException();
        }
    }

    public static void notBlank(String... str){
        for (String s : str) {
            notBlank(s);
        }
    }

    public static void idIsNull(Long id){
        if (!Objects.isNull(id)){
            throw new IllegalParameterException(400,"已经存在Id");
        }
    }

    public static void idIsNotNull(Long id){
        if (Objects.isNull(id)){
            throw new IllegalParameterException(400,"没有id");
        }
    }

    public static <T> void collectionsIsNotNull(Collection<T> collection){
        if (CollUtil.isEmpty(collection)){
            throw new IllegalParameterException(400,"集合为空,不符合要求");
        }
    }
}
