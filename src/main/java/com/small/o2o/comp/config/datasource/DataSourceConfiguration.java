package com.small.o2o.comp.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：  可以动态加载额外的数据源
 * @author: 张小菜
 * @date: 2023/10/24 024 23:35
 * @version: v1.0
 */

@Configuration
public class DataSourceConfiguration {


    @Autowired
    private DruidDataSource dataSource;
    @Autowired
    private InitDataSource initDataSource;

    private List<DataSourceInfo> dataSourceConfigs ;

    @PostConstruct
    public void initializeDataSources() {
        // 这里也可以从数据库加载，可以随时扩充
        //dataSourceConfigs = jdbcTemplate.queryForList("SELECT * FROM SOC_DATASOUCE_INFO", DataSourceInfo.class);
        dataSourceConfigs = initDataSource.getDataSourceList();
        System.out.println("============加载到自定义的数据源=============");
        dataSourceConfigs.stream().forEach(e->{
            System.out.println("------------------");
            System.out.println(JSON.toJSONString(e));
        });
        System.out.println("------------------over");
    }

    @Bean
    public DataSource dynamicDataSource() {
        DynamicDataSource  dynamicDataSource = DynamicDataSource.build();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("DEFAULT", dataSource);

        for (DataSourceInfo config : dataSourceConfigs) {
            DataSource dataSource = createDataSource(config);
            dataSourceMap.put(config.getName(), dataSource);
        }
        //System.out.println(dataSourceMap);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        dynamicDataSource.afterPropertiesSet();
        System.out.println("------------动态数据源初始化成功--------------");
        return dynamicDataSource;
    }




    private DataSource createDataSource(DataSourceInfo config) {
        DruidDataSource  dataSource = new DruidDataSource();

        // 设置数据源配置
        dataSource.setDriverClassName(config.getDriverName());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());

        // 其他配置项，根据需要进行设置
        // 设置连接池大小
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(100);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(20);
        dataSource.setMaxWait(5000);

        // 设置连接相关配置
        if (dataSource.getDriverClassName().toLowerCase().contains("oracle")){
            dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        }else {
            dataSource.setValidationQuery("SELECT 1");
        }
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);

        // 设置逐出策略和时间
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setMaxEvictableIdleTimeMillis(1800000);

        return dataSource;
    }


}
