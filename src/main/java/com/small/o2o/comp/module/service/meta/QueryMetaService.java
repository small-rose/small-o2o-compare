package com.small.o2o.comp.module.service.meta;

import com.small.o2o.comp.config.annotation.DynamicDataSource;
import com.small.o2o.comp.module.service.impl.MetaDataService;
import com.small.o2o.comp.module.service.impl.ServiceStrategyFactory;
import com.small.o2o.comp.module.vo.*;
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
public class QueryMetaService {

    @Autowired
    private ServiceStrategyFactory serviceStrategyFactory ;

    @DynamicDataSource
    public List<ObObjectInfoVO> getObjectInfo(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryObjectInfo();
    }
    @DynamicDataSource
    public List<ObProcedureVO> getProcedureList(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryProcedureVO(queryPramsVO.getType());
    }

    @DynamicDataSource
    public List<ObProcedureVO> queryNameListProcedureVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryNameListProcedureVO(queryPramsVO.getType());
    }


    @DynamicDataSource
    public List<ObSequencesVO> querySequencesVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.querySequencesVO();
    }


    @DynamicDataSource
    public List<ObTableInfoVO> queryTableInfo(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableInfo(queryPramsVO.getTableName());
    }

    @DynamicDataSource
    public List<ObTableColumnFullVO> queryTableColmnFullVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableColmnFullVO(queryPramsVO.getTableName());
    }

    @DynamicDataSource
    public List<IndexExpressions> queryTableIndexExpressions(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableIndexExpressions(queryPramsVO.getTableName());
    }

    @DynamicDataSource
    public List<ObTableIndexVO> queryTableIndexVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableIndexVO(queryPramsVO.getTableName());
    }

    @DynamicDataSource
    public List<ObTablePartitionVO> queryTablePartitionVO(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTablePartitionVO();
    }
    @DynamicDataSource
    public List<ObTablePartitionVO> queryTableReCords(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableReCords();
    }
    @DynamicDataSource
    public List<ObTablePrimaryKeyVO> queryTablePrimaryKeyVO(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTablePrimaryKeyVO(queryPramsVO.getTableName());
    }
    @DynamicDataSource
    public List<ObTypesVO> queryTypesVO(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTypesVO(queryPramsVO.getType());
    }
    @DynamicDataSource
    public List<ObTableViewVO> queryTableView(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableView();
    }
}
