package com.small.o2o.comp.module.vo;

import lombok.Data;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 19:16
 * @version: v1.0
 */

@Data
public class DSCompareVO {

    private String dsFirst;
    private String dsSecond ;

     private String table ;

    /**
     * 比较时可以以mainDs的数据为标准
     * 可在表 和 表的列比较的时生效
     */
    private String mainDs ;

    private boolean tableColumnDDL ;

    private String ext1 ;

}
