package com.small.o2o.comp.config.datasource;

import com.small.o2o.comp.config.factory.YamlAndPropertySourceFactory;
import com.small.o2o.comp.config.pojo.DataSourceInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
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

    private List<DataSourceInfo> dataSourceList = new ArrayList<>();

}
