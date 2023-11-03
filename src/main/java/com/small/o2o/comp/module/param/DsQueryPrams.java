package com.small.o2o.comp.module.param;

import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 22:31
 * @version: v1.0
 */

@Builder
@Data
public class DsQueryPrams {
    /**
     * 当前查询数据源
     */
    private String dataSourceName ;
    /**
     * 当前查询【可选】指定的表
     */
    private String tableName ;
    /**
     *  使用的 元数据类型 动态寻找对应的实现类
     */
    private MetaBuzTypeEnum metaBuzType ;
    /**
     * 当前查询【可选】所使用的 元数据类型参数如 procedure function package
     */
    private String queryParam ;

}
