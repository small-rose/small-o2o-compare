package com.small.o2o.comp.module.service.oracle;

import com.small.o2o.comp.config.annotation.DynamicDataSource;
import com.small.o2o.comp.module.service.impl.ServiceStrategyFactory;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
 * @Description : [ BuzTypeService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/1 21:53
 * @Version ： 1.0
 **/
public class QueryBuzTypeService {


    @Autowired
    private ServiceStrategyFactory serviceStrategyFactory ;

    @DynamicDataSource
    public  <T> List<T> getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz){
        BuzTypeService metaDataService =  serviceStrategyFactory.getBuzTypeServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.getCompareMetaList(queryPramsVO, clazz);
    }

}
