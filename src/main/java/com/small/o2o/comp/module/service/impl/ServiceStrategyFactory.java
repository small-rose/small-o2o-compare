package com.small.o2o.comp.module.service.impl;

import com.small.o2o.comp.config.datasource.InitDataSource;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.exception.BussinessException;
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
    private OracleMetaDataService oracleMetaDataService ;
    @Autowired
    private ObMetaDataService obMetaDataService ;

    @Autowired
    private InitDataSource initDataSource ;

    private static HashMap<String, String> dataSourceMap = new HashMap<>();

    @PostConstruct
    public void init(){
        List<DataSourceInfo> dataSourceList = initDataSource.getDataSourceList();
        dataSourceList.stream().forEach(d->{
            if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.ORACLE.getValue())) {
                dataSourceMap.put(d.getName(), O2OConstants.DBType.ORACLE.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.OB_ORACLE.getValue())) {
                dataSourceMap.put(d.getName(), O2OConstants.DBType.OB_ORACLE.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.OB_ORACLE.getValue())) {
                dataSourceMap.put(d.getName(), O2OConstants.DBType.OB_ORACLE.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.MYSQL.getValue())) {
                dataSourceMap.put(d.getName(), O2OConstants.DBType.MYSQL.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.OB_MYSQL.getValue())) {
                dataSourceMap.put(d.getName(), O2OConstants.DBType.OB_MYSQL.getValue());
            }
        });

    }


    public MetaDataService getMetaServiceStrategy(String dataSourceName) {

        String dsName = dataSourceMap.get(dataSourceName);
        if (O2OConstants.DBType.ORACLE.getValue().equals(dsName)) {
            return oracleMetaDataService;
        } else if (O2OConstants.DBType.OB_ORACLE.getValue().equals(dsName)) {
            return obMetaDataService;
        } /*else if (O2OConstants.DBType.MYSQL.getValue().equals(dsName)) {
            return obMetaDataService;
        } else if (O2OConstants.DBType.OB_MYSQL.getValue().equals(dsName)) {
            return obMetaDataService;
        } */else {
            // 默认策略
            throw new BussinessException("不支持的数据库类型: "+dataSourceName);
        }
    }

}
