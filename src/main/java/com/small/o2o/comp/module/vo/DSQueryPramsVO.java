package com.small.o2o.comp.module.vo;

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
public class DSQueryPramsVO {

    private String dataSourceName ;
    private String metaType ;
    private String tableName ;
    private String queryType ;

}
