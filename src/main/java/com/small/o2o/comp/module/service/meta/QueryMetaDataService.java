package com.small.o2o.comp.module.service.meta;

import com.small.o2o.comp.config.annotation.DynamicDataSource;
import com.small.o2o.comp.module.service.sql.MetaDbTypeSQLService;
import com.small.o2o.comp.module.service.strategy.ServiceStrategyFactory;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTablePartitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 21:53
 * @version: v1.0
 */
@Component
public class QueryMetaDataService {

    @Autowired
    private ServiceStrategyFactory serviceStrategyFactory ;


    public  <T> List<T> queryObjectList(DSQueryPramsVO queryPramsVO, Class clazz){
        MetaDbTypeSQLService metaDataService =  serviceStrategyFactory.getDbTypeServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.getObjectList(queryPramsVO, clazz);
    }


    @DynamicDataSource
    public List<ObTablePartitionVO> queryTablePartitionVO(DSQueryPramsVO queryPramsVO) {
        MetaDbTypeSQLService metaDataService =  serviceStrategyFactory.getDbTypeServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTablePartitionVO();
    }
    @DynamicDataSource
    public List<ObTablePartitionVO> queryTableReCords(DSQueryPramsVO queryPramsVO) {
        MetaDbTypeSQLService metaDataService =  serviceStrategyFactory.getDbTypeServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableReCords();
    }

}
