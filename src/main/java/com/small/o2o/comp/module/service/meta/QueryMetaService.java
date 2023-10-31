package com.small.o2o.comp.module.service.meta;

import com.small.o2o.comp.config.annotation.DynamicDataSource;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.exception.BussinessException;
import com.small.o2o.comp.module.service.impl.MetaDataService;
import com.small.o2o.comp.module.service.impl.ServiceStrategyFactory;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.IndexExpressions;
import com.small.o2o.comp.module.vo.ObObjectInfoVO;
import com.small.o2o.comp.module.vo.ObProcedureVO;
import com.small.o2o.comp.module.vo.ObSequencesVO;
import com.small.o2o.comp.module.vo.ObTableColumnFullVO;
import com.small.o2o.comp.module.vo.ObTableIndexVO;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.ObTablePartitionVO;
import com.small.o2o.comp.module.vo.ObTablePrimaryKeyVO;
import com.small.o2o.comp.module.vo.ObTableViewVO;
import com.small.o2o.comp.module.vo.ObTypesVO;
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
    public  <T> List<T> getObjectList(DSQueryPramsVO queryPramsVO, Class clazz, String queryType){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
         String sql = "";
        switch (queryType) {
            case O2OConstants.SQL_OBJECT :
                sql = metaDataService.queryObjectInfoSQL();
                break;
            case O2OConstants.SQL_TABLE:
                sql = metaDataService.queryTableInfoSQL(queryPramsVO.getTableName());
                break;
             case O2OConstants.SQL_TABLE_COLUMN:
                sql = metaDataService.queryTableColumnFullVoSQL(queryPramsVO.getTableName());
                 break;
            case O2OConstants.SQL_TABLE_INDEX:
                sql = metaDataService.queryTableIndexVoSQL(queryPramsVO.getTableName());
                break;
            case O2OConstants.SQL_TABLE_PRIMARYKEY:
                sql = metaDataService.queryTablePrimaryKeyVoSQL(queryPramsVO.getTableName());
                break;
            case O2OConstants.SQL_VIEW :
                sql = metaDataService.queryTableViewSQL();
                break;
            case O2OConstants.SQL_SEQUENCES:
                sql = metaDataService.querySequencesVoSQL();
                break;
            case O2OConstants.SQL_TYPE:
                sql = metaDataService.queryTypesVoSQL(queryPramsVO.getMetaType());
                break;
            case O2OConstants.SQL_FUNCTION:
            case O2OConstants.SQL_PROCEDURE:
            case O2OConstants.SQL_PACKAGE:
                sql = metaDataService.queryProcedureVoSQL(queryPramsVO.getMetaType());
                break;
            default:
                throw new BussinessException("不支持的枚举查询");
        }

        return  metaDataService.getObjectList(sql, clazz);
    }

    @DynamicDataSource
    public List<ObObjectInfoVO> getObjectInfo(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryObjectInfo();
    }
    @DynamicDataSource
    public List<ObProcedureVO> getProcedureList(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryProcedureVO(queryPramsVO.getMetaType());
    }

    @DynamicDataSource
    public List<ObProcedureVO> queryNameListProcedureVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryNameListProcedureVO(queryPramsVO.getMetaType());
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
    public List<ObTableColumnFullVO> queryTableColumnFullVO(DSQueryPramsVO queryPramsVO){
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableColumnFullVO(queryPramsVO.getTableName());
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
    public Long queryTableCount(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableCount(queryPramsVO.getTableName());
    }

    @DynamicDataSource
    public List<ObTablePrimaryKeyVO> queryTablePrimaryKeyVO(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTablePrimaryKeyVO(queryPramsVO.getTableName());
    }
    @DynamicDataSource
    public List<ObTypesVO> queryTypesVO(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTypesVO(queryPramsVO.getMetaType());
    }
    @DynamicDataSource
    public List<ObTableViewVO> queryTableView(DSQueryPramsVO queryPramsVO) {
        MetaDataService metaDataService =  serviceStrategyFactory.getMetaServiceStrategy(queryPramsVO.getDataSourceName());
        return  metaDataService.queryTableView();
    }




}
