package com.small.o2o.comp.config.datasource;

import com.small.o2o.comp.config.factory.YamlAndPropertySourceFactory;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.exception.DataCheckException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ InitDataSource ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/10/26 20:51
 * @Version ： 1.0
 **/

@Data
@Component
@ConfigurationProperties(prefix = "init")
/* 此处可以动态化 如  value = "classpath:application-${spring.profiles.active}.yml"
*/
@PropertySource(value = "classpath:application.yml", factory = YamlAndPropertySourceFactory.class)
public class InitDataSource {

    @Autowired
    private JdbcTemplate jdbcTemplate ;

    private String dsInitType ;

    private List<DataSourceInfo> dataSourceList = new ArrayList<>();

    private String sql = "select name,driver_Name as driverName,url,username,password,db_Type as dbType from soc_db_info ";

    @PostConstruct
    public void init(){
        if (O2OConstants.DsInitType.CONF.getValue().equals(dsInitType)){
            checkDataSource();
        }else if (O2OConstants.DsInitType.DATABASE.getValue().equals(dsInitType)){
            List<DataSourceInfo> queryList = jdbcTemplate.queryForList(sql, DataSourceInfo.class);
            dataSourceList.addAll(queryList);
            checkDataSource();
        }else {
            throw new DataCheckException("初始化数据源的方式 dsInitType 的值不能识别，可用枚举：[conf,db]");
        }
    }

    public void checkDataSource(){
        List<String> list = dataSourceList.stream().map(DataSourceInfo::getName).collect(Collectors.toList());
        HashSet<String> hashSet = new HashSet<>(list);
        if (list.size() != hashSet.size()) {
            Map<String, Long> counts = list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            List<String> duplicateNames = counts.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            throw new DataCheckException("初始化数据源列表的name "+Arrays.toString(duplicateNames.toArray())+"出现重复值！");
        }
    }
}
