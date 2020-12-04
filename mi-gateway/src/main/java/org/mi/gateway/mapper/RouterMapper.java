package org.mi.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mi.gateway.model.Router;

import java.util.List;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-03 18:42
 **/
public interface RouterMapper extends BaseMapper<Router> {
    List<Router> selectAll();
}
