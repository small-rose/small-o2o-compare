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

    private String name ;
    private String driverName ;
    private String url ;
    private String username ;
    private String password ;
    private String dbType ;
    private String dbDesc ;
    private String ifValid ;
    private String ext1 ;
    private String ext2 ;

    @Override
    public String toString() {
        return "DataSourceInfo{" +
                ", name='" + name + '\'' +
                ", driverName='" + driverName + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ifValid='" + ifValid + '\'' +
                ", dbType='" + dbType + '\'' +
                ", dbDesc='" + dbDesc + '\'' +
                '}';
    }
}
