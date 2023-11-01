package com.small.o2o.comp.config.datasource;

import com.alibaba.fastjson2.JSON;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.exception.DataCheckException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：  可以动态加载额外的数据源
 * @author: 张小菜
 * @date: 2023/10/24 024 23:35
 * @version: v1.0
 */
@Slf4j
@Component
public class DataSourceTypeConfig {

    @Autowired
    private InitDataSource initDataSource;

    @Getter
    private HashMap dataSourceTypeMap = new HashMap();

    @PostConstruct
    public void initializeDataSources() {

        List<DataSourceInfo>  dataSourceConfigs = initDataSource.getDataSourceList();
        log.info("=====加载到自定义的数据源数据库类型=====");
        dataSourceConfigs.stream().forEach(d->{
             System.out.println(" 解析到数据源 >>> "+JSON.toJSONString(d));
            if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.ORACLE.getValue())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), O2OConstants.DBType.ORACLE.getValue());
                log.info("============识别到自定义的数据源数据库类型>>> "+O2OConstants.DBType.ORACLE.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.OB_ORACLE.getValue())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), O2OConstants.DBType.OB_ORACLE.getValue());
                log.info("============识别到自定义的数据源数据库类型>>> "+O2OConstants.DBType.OB_ORACLE.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.MYSQL.getValue())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), O2OConstants.DBType.MYSQL.getValue());
                log.info("============识别到自定义的数据源数据库类型>>> "+O2OConstants.DBType.MYSQL.getValue());
            }else  if(d.getDriverName().toUpperCase().contains(O2OConstants.DBType.OB_MYSQL.getValue())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), O2OConstants.DBType.OB_MYSQL.getValue());
                log.info("============识别到自定义的数据源数据库类型>>> "+O2OConstants.DBType.OB_MYSQL.getValue());
            }else {
                throw new DataCheckException("暂不支持比较的数据源！");
            }
        });
        log.info("=====加载到自定义的数据源数据库类型完成=====");
    }


}
