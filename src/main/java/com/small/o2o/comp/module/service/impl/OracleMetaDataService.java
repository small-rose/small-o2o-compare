package com.small.o2o.comp.module.service.impl;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.exception.BussinessException;
import com.small.o2o.comp.module.service.meta.JdbcTemplateService;
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
import com.small.o2o.comp.module.vo.OraTableYasuoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class OracleMetaDataService  implements MetaDbTypeSQLService {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;


    @Override
    public String getDbType() {
        return O2OConstants.DBType.ORACLE.getValue();
    }

    @Override
    public <T> List<T> getObjectList(DSQueryPramsVO pramsVO , Class clazz) {
        Assert.hasText(pramsVO.getQueryType(),"ORACLE请求参数pramsVO缺少查询元数据类型queryType赋值!");
        String sql = "";
        switch (pramsVO.getQueryType()) {
            case O2OConstants.SQL_OBJECT :
                sql = queryObjectInfoSQL();
                break;
            case O2OConstants.SQL_TABLE:
                sql = queryTableInfoSQL(pramsVO.getTableName());
                break;
            case O2OConstants.SQL_TABLE_COLUMN:
                sql = queryTableColumnFullVoSQL(pramsVO.getTableName());
                break;
            case O2OConstants.SQL_TABLE_INDEX:
                sql = queryTableIndexVoSQL(pramsVO.getTableName());
                break;
            case O2OConstants.SQL_TABLE_PRIMARYKEY:
                sql = queryTablePrimaryKeyVoSQL(pramsVO.getTableName());
                break;
            case O2OConstants.SQL_VIEW :
                sql = queryTableViewSQL();
                break;
            case O2OConstants.SQL_SEQUENCES:
                sql = querySequencesVoSQL();
                break;
            case O2OConstants.SQL_TYPE:
                sql = queryTypesVoSQL(pramsVO.getMetaType());
                break;
            case O2OConstants.SQL_FUNCTION:
            case O2OConstants.SQL_PROCEDURE:
            case O2OConstants.SQL_PACKAGE:
                sql = queryProcedureVoSQL(pramsVO.getMetaType());
                break;
            default:
                throw new BussinessException("不支持的元数据枚举查询");
        }
        System.out.println("ORACLE "+pramsVO.getQueryType()+" >>> SQL \n " + sql);
        return jdbcTemplateService.queryForList(sql, clazz);
    }

    @Override
    public String queryObjectInfoSQL() {
        return  "SELECT OBJECT_TYPE, COUNT FROM " +
                "(SELECT OBJECT_TYPE, COUNT(1) COUNT FROM USER_OBJECTS  GROUP BY OBJECT_TYPE --ORDER BY OBJECT_TYPE\n" +
                "    UNION ALL\n" +
                "SELECT 'INDEX-' || UNIQUENESS  OBJECT_TYPE , COUNT(1) COUNT FROM USER_INDEXES GROUP BY UNIQUENESS)\n" +
                "ORDER BY OBJECT_TYPE " ;
    }

    @Override
    public String queryTableInfoSQL(String tableName) {
        String sql = "SELECT t1.TABLE_NAME , t2.COMMENTS , t1.STATUS , t1.TEMPORARY  \n" +
                "FROM USER_ALL_TABLES t1 join USER_TAB_COMMENTS t2 on t1.TABLE_NAME=t2.TABLE_NAME  WHERE T2.TABLE_TYPE='TABLE' ";
        return sql;
    }

    @Override
    public String queryTableColumnFullVoSQL(String tableName) {
        String sql = "SELECT  TC.TABLE_NAME,  TC.COLUMN_NAME,\n" +
                "       CASE  WHEN TC.DATA_TYPE='DATE' THEN  TC.DATA_TYPE\n" +
                "            WHEN TC.DATA_TYPE='NUMBER' THEN  " +
                "            ( CASE WHEN  TC.DATA_PRECISION IS NOT NULL THEN\n" +
                "                TC.DATA_TYPE || '(' || TC.DATA_PRECISION || ','|| TC.DATA_SCALE ||')'\n" +
                "                    ELSE TC.DATA_TYPE END)" +
                "            WHEN TC.CHARACTER_SET_NAME IS NOT NULL THEN  TC.DATA_TYPE ||'(' || TC.CHAR_LENGTH || ')' \n" +
                "            ELSE    TC.DATA_TYPE || '(' || TC.DATA_LENGTH || ')' END  DATA_TYPE,\n" +
                "       CASE TC.NULLABLE WHEN 'N' THEN\n" +
                "           ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  'NOT NULL'\n" +
                "                ELSE ('DEFAULT %s NOT NULL') END )\n" +
                "           ELSE ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  ''\n" +
                "                ELSE ('DEFAULT %s ') END) END EXTEND,\n" +
                "       TC.NULLABLE, TC.DATA_DEFAULT,\n" +
                "       TC.COLUMN_ID\n" +
                "FROM USER_TAB_COLUMNS TC ";//+
        //"WHERE TABLE_NAME ='AMS_ACCOUNTCHECK1_TD'\n" +
        //"ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        if (StringUtils.hasText(tableName)){
            sql += "WHERE TC.TABLE_NAME = '"+tableName+"' ";
        }
        sql += "ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        return sql;
    }

    @Override
    public String queryTableIndexVoSQL(String tableName) {
        String sql = "SELECT T.TABLE_NAME,T.INDEX_NAME,\n" +
                "LISTAGG(CASE I.INDEX_TYPE WHEN 'NORMAL' THEN T.COLUMN_NAME ||' ' || T.DESCEND\n" +
                "    ELSE T.COLUMN_NAME END , ',')WITHIN GROUP(ORDER BY T.TABLE_NAME, T.COLUMN_NAME) AS COLUMN_NAME\n" +
                "       ,I.INDEX_TYPE, I.UNIQUENESS\n" +
                "FROM USER_IND_COLUMNS T,USER_INDEXES I WHERE T.INDEX_NAME = I.INDEX_NAME AND T.TABLE_NAME = I.TABLE_NAME\n"  ;
        if (StringUtils.hasText(tableName)){
            sql += " AND I.TABLE_NAME = '"+tableName+"'" ;
        }
        sql += " GROUP BY  T.TABLE_NAME,T.INDEX_NAME ,I.INDEX_TYPE, I.UNIQUENESS ORDER BY T.TABLE_NAME,T.INDEX_NAME " ;

        return sql;
    }

    @Override
    public String queryTablePrimaryKeyVoSQL(String tableName) {
        String sql = "select cu.TABLE_NAME ,cu.CONSTRAINT_NAME ,LISTAGG(cu.COLUMN_NAME, ',')WITHIN GROUP(ORDER BY cu.TABLE_NAME,cu.COLUMN_NAME) as COLUMN_NAME " +
                "from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " ;

        if (StringUtils.hasText(tableName)){
            sql += " AND cu.TABLE_NAME = '"+tableName+"' " ;
        }
        sql += " GROUP BY  cu.TABLE_NAME,cu.CONSTRAINT_NAME ORDER BY cu.TABLE_NAME, cu.CONSTRAINT_NAME " ;

        return sql;
    }

    @Override
    public String queryTableViewSQL() {
        String sql = "SELECT VIEW_NAME, TEXT_LENGTH, TEXT FROM USER_VIEWS " ;
        return sql;
    }

    @Override
    public String querySequencesVoSQL() {
        String sql = "SELECT T.SEQUENCE_NAME, T.LAST_NUMBER  FROM USER_SEQUENCES T ORDER BY T.SEQUENCE_NAME " ;
        return sql;
    }

    @Override
    public String queryTypesVoSQL(String metaType) {
        String sql = "SELECT TYPE_NAME, TYPECODE FROM USER_TYPES " ;
        if (StringUtils.hasText(metaType)){
            sql = sql.concat("WHERE TYPECODE = '").concat(metaType.toUpperCase()).concat("' ");
        }
        return sql;
    }

    @Override
    public String queryProcedureVoSQL(String metaType) {
        String sql = "SELECT OBJECT_TYPE, OBJECT_NAME, PROCEDURE_NAME FROM user_procedures  " ;
        if (StringUtils.hasText(metaType)){
            sql = sql.concat("WHERE OBJECT_TYPE = '").concat(metaType.toUpperCase()).concat("' ");
        }
        sql = sql.concat("order by OBJECT_TYPE, OBJECT_NAME, SUBPROGRAM_ID");
        return sql;
    }
    @Override
    public String queryPkgProcedureNameListSQL(String metaType) {
        String sql = "SELECT OBJECT_TYPE, OBJECT_NAME, PROCEDURE_NAME FROM user_procedures  " ;
        if (StringUtils.hasText(metaType)){
            sql = sql.concat("WHERE OBJECT_TYPE = '").concat(metaType.toUpperCase()).concat("' ");
        }
        sql = sql.concat("order by OBJECT_TYPE, OBJECT_NAME, SUBPROGRAM_ID");
        return sql;
    }

    @Override
    public List<ObObjectInfoVO> queryObjectInfo() {
        String sql = "SELECT OBJECT_TYPE, COUNT FROM " +
                "(SELECT OBJECT_TYPE, COUNT(1) COUNT FROM USER_OBJECTS  GROUP BY OBJECT_TYPE --ORDER BY OBJECT_TYPE\n" +
                "    UNION ALL\n" +
                "SELECT 'INDEX-' || UNIQUENESS  OBJECT_TYPE , COUNT(1) COUNT FROM USER_INDEXES GROUP BY UNIQUENESS)\n" +
                "ORDER BY OBJECT_TYPE " ;
        return jdbcTemplateService.queryForList(sql, ObObjectInfoVO.class);
    }

    /**
     * 查表
     * @return
     */
    @Override
    public List<ObTableInfoVO> queryTableInfo(String tableType){
        String sql = "SELECT t1.TABLE_NAME , t2.COMMENTS , t1.STATUS , t1.TEMPORARY  " +
                "FROM USER_ALL_TABLES t1 join USER_TAB_COMMENTS t2 on t1.TABLE_NAME=t2.TABLE_NAME  WHERE T2.TABLE_TYPE='TABLE' ";
       /* if (StringUtils.hasText(tableType)){
            sql = sql.concat("WHERE T2.TABLE_TYPE='").concat(tableType.toUpperCase()).concat("' ");
        }*/
        return jdbcTemplateService.queryForList(sql, ObTableInfoVO.class);
    }

    /**
     * 查询表对应的列完整版
     *
     */
    @Override
    public List<ObTableColumnFullVO> queryTableColumnFullVO(String tableName){
        String sql = "SELECT  TC.TABLE_NAME,  TC.COLUMN_NAME,\n" +
                "       CASE  WHEN TC.DATA_TYPE='DATE' THEN  TC.DATA_TYPE\n" +
                "            WHEN TC.DATA_TYPE='NUMBER' THEN  " +
                "            ( CASE WHEN  TC.DATA_PRECISION IS NOT NULL THEN\n" +
                "                TC.DATA_TYPE || '(' || TC.DATA_PRECISION || ','|| TC.DATA_SCALE ||')'\n" +
                "                    ELSE TC.DATA_TYPE END)" +
                "            WHEN TC.CHARACTER_SET_NAME IS NOT NULL THEN  TC.DATA_TYPE ||'(' || TC.CHAR_LENGTH || ')' \n" +
                "            ELSE    TC.DATA_TYPE || '(' || TC.DATA_LENGTH || ')' END  DATA_TYPE,\n" +
                "       CASE TC.NULLABLE WHEN 'N' THEN\n" +
                "           ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  'NOT NULL'\n" +
                "                ELSE ('DEFAULT %s NOT NULL') END )\n" +
                "           ELSE ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  ''\n" +
                "                ELSE ('DEFAULT %s ') END) END EXTEND,\n" +
                "       TC.NULLABLE, TC.DATA_DEFAULT,\n" +
                "       TC.COLUMN_ID\n" +
                "FROM USER_TAB_COLUMNS TC ";//+
                //"WHERE TABLE_NAME ='AMS_ACCOUNTCHECK1_TD'\n" +
                //"ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        if (StringUtils.hasText(tableName)){
            sql += "WHERE TC.TABLE_NAME = '"+tableName+"' ";
        }
        sql += "ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        //log.info("oracle sql: ====\n {}", sql);
        return jdbcTemplateService.queryForList(sql, ObTableColumnFullVO.class);
    }



    /**
     * 查表对应的分区数
     * @return
     */
    @Override
    public List<ObTablePartitionVO> queryTablePartitionVO(){
        String sql = "SELECT TABLE_NAME, COUNT(*) count FROM SYS.USER_TAB_PARTITIONS GROUP BY TABLE_NAME" ;
        return jdbcTemplateService.queryForList(sql, ObTablePartitionVO.class);
    }

    /**
     * 查表对应的记录数
     * @return
     */
    @Override
    public List<ObTablePartitionVO> queryTableReCords(){
        String sql = "SELECT TABLE_NAME, NUM_ROWS  COUNT FROM SYS.USER_TABLES " ;
        return jdbcTemplateService.queryForList(sql, ObTablePartitionVO.class);
    }

    /**
     * 查 序列
     * @return
     */
    @Override
    public List<ObSequencesVO> querySequencesVO(){
        String sql = "SELECT T.SEQUENCE_NAME, T.LAST_NUMBER  FROM USER_SEQUENCES T ORDER BY T.SEQUENCE_NAME " ;
        return jdbcTemplateService.queryForList(sql, ObSequencesVO.class);
    }

    /**
     * 查 表主键
     * @return
     */
    @Override
    public List<ObTablePrimaryKeyVO> queryTablePrimaryKeyVO(String tableName){
        String sql = "select cu.TABLE_NAME ,cu.CONSTRAINT_NAME ,LISTAGG(cu.COLUMN_NAME, ',')WITHIN GROUP(ORDER BY cu.TABLE_NAME,cu.COLUMN_NAME) as COLUMN_NAME " +
                "from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " ;

        if (StringUtils.hasText(tableName)){
            sql += " AND cu.TABLE_NAME = '"+tableName+"' " ;
        }
        sql += " GROUP BY  cu.TABLE_NAME,cu.CONSTRAINT_NAME ORDER BY cu.TABLE_NAME, cu.CONSTRAINT_NAME " ;
        return jdbcTemplateService.queryForList(sql, ObTablePrimaryKeyVO.class);
    }

    /**
     * 查 表索引-函数式索引表达式
     * @return
     */
    @Override
    public List<IndexExpressions> queryTableIndexExpressions(String tableName){
        String sql = " SELECT T.TABLE_NAME, T.INDEX_NAME,\n" +
                "       LISTAGG(COLUMN_EXPRESSION ||' ASC',',')WITHIN GROUP(ORDER BY T.TABLE_NAME, T.INDEX_NAME,T.COLUMN_EXPRESSION) AS COLUMN_EXPRESSION\n" +
                "FROM (SELECT E.TABLE_NAME,E.INDEX_NAME,INDEX_COLUMN_EXPRESSION(E.TABLE_NAME, E.INDEX_NAME,E.COLUMN_POSITION) AS COLUMN_EXPRESSION\n" +
                "    FROM  USER_IND_EXPRESSIONS E) T\n" ;

        if (StringUtils.hasText(tableName)){
            sql += " AND T.TABLE_NAME = '"+tableName+"' " ;
        }
        sql += " GROUP BY T.TABLE_NAME, T.INDEX_NAME  ";
        return jdbcTemplateService.queryForList(sql, IndexExpressions.class);
    }
    /**
     * 查 表索引
     * @return
     */
    @Override
    public List<ObTableIndexVO> queryTableIndexVO(String tableName){
        String sql = "SELECT T.TABLE_NAME,T.INDEX_NAME,\n" +
                "LISTAGG(CASE I.INDEX_TYPE WHEN 'NORMAL' THEN T.COLUMN_NAME ||' ' || T.DESCEND\n" +
                "    ELSE T.COLUMN_NAME END , ',')WITHIN GROUP(ORDER BY T.TABLE_NAME, T.COLUMN_NAME) AS COLUMN_NAME\n" +
                "       ,I.INDEX_TYPE, I.UNIQUENESS\n" +
                "FROM USER_IND_COLUMNS T,USER_INDEXES I WHERE T.INDEX_NAME = I.INDEX_NAME AND T.TABLE_NAME = I.TABLE_NAME\n"  ;
        if (StringUtils.hasText(tableName)){
            sql += " AND I.TABLE_NAME = '"+tableName+"'" ;
        }
        sql += " GROUP BY  T.TABLE_NAME,T.INDEX_NAME ,I.INDEX_TYPE, I.UNIQUENESS ORDER BY T.TABLE_NAME,T.INDEX_NAME " ;
        return jdbcTemplateService.queryForList(sql, ObTableIndexVO.class);
    }


    @Override
    public List<ObTableViewVO> queryTableView() {
        String sql = "SELECT VIEW_NAME, TEXT_LENGTH, TEXT FROM USER_VIEWS " ;
        return jdbcTemplateService.queryForList(sql, ObTableViewVO.class);
    }

    /**
     * 查 PACKAGE/PROCEDURE/FUNCTION
     * @return
     */
    @Override
    public List<ObProcedureVO> queryProcedureVO(String type){
        String sql = "SELECT OBJECT_TYPE, OBJECT_NAME, PROCEDURE_NAME FROM user_procedures  " ;
        if (StringUtils.hasText(type)){
            sql = sql.concat("WHERE OBJECT_TYPE = '").concat(type.toUpperCase()).concat("' ");
        }
        sql = sql.concat("order by OBJECT_TYPE, OBJECT_NAME, SUBPROGRAM_ID");
        return jdbcTemplateService.queryForList(sql, ObProcedureVO.class);
    }


    /**
     * 查 PACKAGE/PROCEDURE/FUNCTION 的 Name
     *
     * @return
     */
    @Override
    public List<ObProcedureVO> queryNameListProcedureVO(String type){
        String sql = "SELECT distinct OBJECT_TYPE, OBJECT_NAME FROM user_procedures  " ;
        if (StringUtils.hasText(type)){
            sql = sql.concat("WHERE OBJECT_TYPE = '").concat(type.toUpperCase()).concat("' ");
        }
        sql = sql.concat("order by OBJECT_TYPE, OBJECT_NAME");
        return jdbcTemplateService.queryForList(sql, ObProcedureVO.class);
    }

    /**
     * 查 TYPE 集合
     * @return
     */
    @Override
    public List<ObTypesVO> queryTypesVO(String type){
        String sql = "SELECT TYPE_NAME, TYPECODE FROM USER_TYPES " ;
        if (StringUtils.hasText(type)){
            sql = sql.concat("WHERE TYPECODE = '").concat(type.toUpperCase()).concat("' ");
        }

        return jdbcTemplateService.queryForList(sql, ObTypesVO.class);
    }

    /**
     * 查表数据量
     * @param tableName
     * @return
     */
    public  List<OraTableYasuoVo> queryTableYasuo(String tableName){
        String sql = "select  TABLE_NAME, COMPRESSION from USER_TABLES ";
        if (StringUtils.hasText(tableName)){
            sql += " WHERE TABLE_NAME = '"+tableName+"' ";
        };
        return jdbcTemplateService.queryForList(sql, OraTableYasuoVo.class);
    }


    /**
     * 查表是否压缩了
     * @param tableName
     * @return
     */
    @Override
    public Long queryTableCount(String tableName){
        String sql = "SELECT COUNT(1) FROM  "+tableName;
        return jdbcTemplateService.queryOneColumn(sql, Long.class);
    }
}
