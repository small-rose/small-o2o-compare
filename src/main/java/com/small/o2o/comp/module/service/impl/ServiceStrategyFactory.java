package com.small.o2o.comp.module.service.impl;

import com.small.o2o.comp.config.datasource.DataSourceTypeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 21:53
 * @version: v1.0
 */
@Component
public class ServiceStrategyFactory {

    @Autowired
    private List<MetaDataService> metaDataServiceList ;

    @Autowired
    private DataSourceTypeConfig dataSourceTypeConfig ;

    private HashMap<String, String> dataSourceMap = new HashMap<>();

    @PostConstruct
    public void init(){
        dataSourceMap.putAll(dataSourceTypeConfig.getDataSourceTypeMap());
    }


    public MetaDataService getMetaServiceStrategy(String dataSourceName) {

        String dsName = dataSourceMap.get(dataSourceName);
        return metaDataServiceList.stream().filter(s->(dsName.equalsIgnoreCase(s.getDbType()))).findFirst().get();

       /* if (O2OConstants.DBType.ORACLE.getValue().equals(dsName)) {
            return oracleMetaDataService;
        } else if (O2OConstants.DBType.OB_ORACLE.getValue().equals(dsName)) {
            return obMetaDataService;
        } else if (O2OConstants.DBType.MYSQL.getValue().equals(dsName)) {
            return obMetaDataService;
        } else if (O2OConstants.DBType.OB_MYSQL.getValue().equals(dsName)) {
            return obMetaDataService;
        }else {
            // 默认策略
            throw new BussinessException("不支持的数据库类型: "+dataSourceName);
        }*/
    }

}
