package com.small.o2o.comp.module.service.oracle;

import lombok.Data;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/5 005 15:44
 * @version: v1.0
 */
@Data
public abstract class MetaConditionTable {

    protected List<String> includeList ;

    protected List<String> excludeList ;

    public void setIncludeTabList(List<String> tabList){
        this.includeList = tabList;
    }

    public void setExcludeTabList(List<String>  tabList){
        this.excludeList = tabList;
    }
}
