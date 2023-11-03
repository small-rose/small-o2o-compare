package com.small.o2o.comp.config.datasource;

import com.alibaba.fastjson2.JSON;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import com.small.o2o.comp.core.enums.DBType;
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
 * TODO 描述：  初始化数据源的数据库类型
 * @author: 张小菜
 * @date: 2023/10/24 024 23:35
 * @version: v1.0
 */
@Slf4j
@Component
public class InitDataSourceType {

    @Autowired
    private InitDataSource initDataSource;

    @Getter
    private HashMap dataSourceTypeMap = new HashMap();

    @PostConstruct
    public void initializeDataSources() {

        List<DataSourceInfo>  dataSourceConfigs = initDataSource.getDataSourceList();
        dataSourceConfigs.stream().forEach(d->{
             System.out.println(" 解析到数据源 >>> "+JSON.toJSONString(d));
            if(d.getDbType().toUpperCase().contains(DBType.ORACLE.name())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(),  DBType.ORACLE.name());
                log.info("Init dbType >>> 识别到自定义的数据源数据库类型>>> "+DBType.ORACLE.name());
            }else  if(d.getDbType().toUpperCase().contains(DBType.OB_ORACLE.name())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), DBType.OB_ORACLE.name());
                log.info("Init dbType >>> 识别到自定义的数据源数据库类型>>> "+DBType.OB_ORACLE.name());
            }else  if(d.getDbType().toUpperCase().contains(DBType.MYSQL.name())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), DBType.MYSQL.name());
                log.info("Init dbType >>> 识别到自定义的数据源数据库类型>>> "+DBType.MYSQL.name());
            }else  if(d.getDbType().toUpperCase().contains(DBType.OB_MYSQL.name())) {
                dataSourceTypeMap.put(d.getName().toUpperCase(), DBType.OB_MYSQL.name());
                log.info("Init dbType >>> 识别到自定义的数据源数据库类型>>> "+DBType.OB_MYSQL.name());
            }else {
                throw new DataCheckException("目前支持配置的数据源dbType枚举[ORACLE,OB_ORACLE,MYSQL,OB_MYSQL],读取到非法配置："+d.getDbType());
            }
        });
        log.info("Init dbType success !");
    }


}
