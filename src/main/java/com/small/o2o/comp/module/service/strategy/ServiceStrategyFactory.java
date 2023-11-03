package com.small.o2o.comp.module.service.strategy;

import com.small.o2o.comp.config.datasource.InitDataSourceType;
import com.small.o2o.comp.core.enums.DBTypeEnum;
import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.service.oracle.MetaBuzTypeService;
import com.small.o2o.comp.module.service.sql.MetaDbTypeSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：SERVICE 策略路由
 * @author: 张小菜
 * @date: 2023/10/29 029 21:53
 * @version: v1.0
 */
@Component
public class ServiceStrategyFactory {

    @Autowired
    private List<MetaDbTypeSQLService> metaDataServiceList ;

    @Autowired
    private List<MetaBuzTypeService> buzTypeServiceList ;


    @Autowired
    private InitDataSourceType dstConfig ;

    private HashMap<String, DBTypeEnum> dataSourceMap = new HashMap<>();

    @PostConstruct
    public void init(){
        dataSourceMap.putAll(dstConfig.getDataSourceTypeMap());
    }


    public MetaDbTypeSQLService getDbTypeServiceStrategy(String dataSourceName) {

        DBTypeEnum dsName = dataSourceMap.get(dataSourceName);
        return metaDataServiceList.stream().filter(s->(dsName.equals(s.getDbType()))).findFirst().get();
    }


    public MetaBuzTypeService getBuzTypeServiceStrategy(MetaBuzTypeEnum buzType) {

         return buzTypeServiceList.stream().filter(s->(buzType.equals(s.getBuzType()))).findFirst().get();
    }
}
