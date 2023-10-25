package com.small.o2o.comp.config.pojo;

import lombok.Data;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/26 026 0:18
 * @version: v1.0
 */

@Data
public class DataSourceInfo {

    private int id ;
    private String name ;
    private String driverName ;
    private String url ;
    private String username ;
    private String password ;
    private String ifValid ;
    private String ext1 ;
    private String ext2 ;
}
