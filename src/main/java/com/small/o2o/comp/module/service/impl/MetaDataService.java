package com.small.o2o.comp.module.service.impl;

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

import java.util.List;

public interface MetaDataService {


    public String getDbType();


    /**
     *
     * 查基本的对象信息
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getObjectList(String sql ,Class clazz);

    /**
     * 查基本的对象信息
     * @return
     */
    public String queryObjectInfoSQL();

    String queryTableInfoSQL(String tableName);

    String queryTableColumnFullVoSql(String tableName);

    String queryTableIndexVoSQL(String tableName);

    String queryTablePrimaryKeyVoSQL(String tableName);

    String queryTableViewSQL();

    String querySequencesVoSQL();

    String queryTypesVoSQL(String metaType);

    String queryProcedureVoSQL(String metaType);

    /**
     * 查基本的对象信息
     * @return
     */
    public List<ObObjectInfoVO> queryObjectInfo();


    
    /**
     * 查表 或 视图
     * @return
     */
    public List<ObTableInfoVO> queryTableInfo(String tabType);

    /**
     * 查询表对应的列完整版
     *
     */
    public List<ObTableColumnFullVO> queryTableColumnFullVO(String tableName);

 

    /**
     * 查表对应的分区数
     * @return
     */
    public List<ObTablePartitionVO> queryTablePartitionVO();

    /**
     * 查表对应的记录数
     * @return
     */
    public List<ObTablePartitionVO> queryTableReCords();

    /**
     * 查 序列
     * @return
     */
    public List<ObSequencesVO> querySequencesVO();
    /**
     * 查 表主键
     * @return
     */
    public List<ObTablePrimaryKeyVO> queryTablePrimaryKeyVO(String tableName);

    /**
     * 查 表索引-函数式索引表达式
     * @return
     */
    public List<IndexExpressions> queryTableIndexExpressions(String tableName);

    /**
     * 查 表索引
     * @return
     */
    public List<ObTableIndexVO> queryTableIndexVO(String tableName);
    /**
     * 查 表试图
     * @return
     */
    public List<ObTableViewVO> queryTableView();

    /**
     * 查 PACKAGE/PROCEDURE/FUNCTION
     *
     * @return
     */
    public List<ObProcedureVO> queryProcedureVO(String type);

    /**
     * 查 PACKAGE/PROCEDURE/FUNCTION 的 Name
     *
     * @return
     */
    public List<ObProcedureVO> queryNameListProcedureVO(String type);

    /**
     * 查 TYPE 集合
     * @return
     */
    public List<ObTypesVO> queryTypesVO(String type);

    /**
     * 查表数据量
     * @param tableName
     * @return
     */
    public Long queryTableCount(String tableName);



}
