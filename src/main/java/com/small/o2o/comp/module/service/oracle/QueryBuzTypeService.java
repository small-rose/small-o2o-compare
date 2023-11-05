package com.small.o2o.comp.module.service.oracle;

import com.small.o2o.comp.config.annotation.DynamicDataSource;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.strategy.ServiceStrategyFactory;
import com.small.o2o.comp.module.param.DsQueryPrams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ BuzTypeService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/1 21:53
 * @Version ： 1.0
 **/

@Service
public class QueryBuzTypeService {


    @Autowired
    private ServiceStrategyFactory serviceStrategyFactory ;

    @DynamicDataSource
    public  <T> List<T> getCompareMetaList(DsQueryPrams queryPramsVO){
        MetaBuzTypeService metaDataService =  serviceStrategyFactory.getBuzTypeServiceStrategy(queryPramsVO.getMetaBuzType());
        if (metaDataService instanceof MetaConditionTable){
            DsCompareParam dsCompare = MetaDataContextHolder.getDsCompare();
            ((MetaConditionTable) metaDataService).setIncludeList(dsCompare.getIncludeTabList());
            ((MetaConditionTable) metaDataService).setExcludeList(dsCompare.getExcludeTabList());
        }
        return  metaDataService.getCompareMetaList(queryPramsVO);
    }

}
