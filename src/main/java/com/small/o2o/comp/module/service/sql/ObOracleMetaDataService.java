package com.small.o2o.comp.module.service.sql;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.core.enums.DBType;
import com.small.o2o.comp.core.exception.BussinessException;
import com.small.o2o.comp.module.service.meta.JdbcTemplateService;
import com.small.o2o.comp.module.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/1 029 1:37
 * @version: v1.0
 */
@Slf4j
@Service
public class ObOracleMetaDataService implements MetaDbTypeSQLService {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;


    @Override
    public String getDbType() {
        return  DBType.OB_ORACLE.name();
    }

    @Override
    public <T> List<T> getObjectList(DSQueryPramsVO pramsVO ,Class clazz) {
        Assert.hasText(pramsVO.getQueryType(),"OB_ORACLE请求参数pramsVO缺少查询元数据类型queryType赋值!");
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
        System.out.println("OB_ORACLE "+pramsVO.getQueryType()+" >>> SQL \n " + sql);
        return jdbcTemplateService.queryForList(sql, clazz);
     }

    @Override
    public String queryObjectInfoSQL() {
        return "SELECT OBJECT_TYPE, COUNT FROM " +
                "(SELECT OBJECT_TYPE, COUNT(1) COUNT FROM USER_OBJECTS  GROUP BY OBJECT_TYPE --ORDER BY OBJECT_TYPE\n" +
                "    UNION ALL\n" +
                "SELECT 'INDEX-' || UNIQUENESS  OBJECT_TYPE , COUNT(1) COUNT FROM USER_INDEXES GROUP BY UNIQUENESS)\n" +
                "ORDER BY OBJECT_TYPE " ;
    }

    @Override
    public String queryTableInfoSQL(String tableName) {
        return "SELECT t1.TABLE_NAME , t2.COMMENTS , t1.STATUS , t1.TEMPORARY ,T2.TABLE_TYPE " +
                "FROM USER_ALL_TABLES t1 join USER_TAB_COMMENTS t2 on t1.TABLE_NAME=t2.TABLE_NAME WHERE T2.TABLE_TYPE='TABLE' " ;
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
                "            ELSE  TC.DATA_TYPE || '(' || TC.DATA_LENGTH || ')' END  DATA_TYPE,\n" +
                "       CASE TC.NULLABLE WHEN 'N' THEN\n" +
                "           ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  'NOT NULL'\n" +
                "                ELSE ('DEFAULT '|| TC.DATA_DEFAULT || ' NOT NULL') END )\n" +
                "           ELSE ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  ''\n" +
                "                ELSE ('DEFAULT '|| TC.DATA_DEFAULT || '') END) END EXTEND,\n" +
                "       TC.NULLABLE, TC.DATA_DEFAULT,\n" +
                "       TC.COLUMN_ID\n" +
                "FROM USER_TAB_COLUMNS TC   " ;//+
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
        String sql = "SELECT T.TABLE_NAME, T.INDEX_NAME,\n" +
                "       LISTAGG(CASE\n" +
                "                   WHEN INSTR(I.INDEX_TYPE, 'FUNCTION') > 0 THEN NEWCOLUMN_NAME\n" +
                "                   ELSE T.COLUMN_NAME || ' ' || T.DESCEND END, ',')\n" +
                "               WITHIN GROUP (ORDER BY T.TABLE_NAME,T.INDEX_NAME,T.COLUMN_POSITION) AS COLUMN_NAME,\n" +
                "       I.INDEX_TYPE,\n" +
                "       I.UNIQUENESS\n" +
                "FROM (SELECT N.*,\n" +
                "             (CASE\n" +
                "                  WHEN INSTR(N.COLUMN_NAME, 'SYS_NC') > 0 THEN\n" +
                "                      (SELECT INDEX_COLUMN_EXPRESSION(E.TABLE_NAME, E.INDEX_NAME, E.COLUMN_POSITION)\n" +
                "                       FROM USER_IND_EXPRESSIONS E\n" +
                "                       WHERE E.INDEX_NAME = N.INDEX_NAME AND E.COLUMN_POSITION = N.COLUMN_POSITION)\n" +
                "                  ELSE N.COLUMN_NAME END) AS NEWCOLUMN_NAME\n" +
                "      FROM USER_IND_COLUMNS N\n" ;
        if (StringUtils.hasText(tableName)) {
            sql+= "    WHERE N.TABLE_NAME = 'ACT_RU_CASE_EXECUTION'\n" ;
        }
        sql+= "    ) T, USER_INDEXES I\n" +
                "WHERE T.INDEX_NAME = I.INDEX_NAME\n" +
                "  AND T.TABLE_NAME = I.TABLE_NAME\n" +
                "GROUP BY T.TABLE_NAME, T.INDEX_NAME, I.INDEX_TYPE, I.UNIQUENESS\n" +
                "ORDER BY T.TABLE_NAME, T.INDEX_NAME "  ;
        return sql;
    }

    @Override
    public String queryTablePrimaryKeyVoSQL(String tableName) {
        String sql = "select cu.TABLE_NAME ,cu.CONSTRAINT_NAME ,LISTAGG(cu.COLUMN_NAME, ',')WITHIN GROUP(ORDER BY cu.TABLE_NAME,cu.COLUMN_NAME) as COLUMN_NAME " +
                "from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " ;

        if (StringUtils.hasText(tableName)){
            sql += " AND cu.TABLE_NAME = '"+tableName+"'" ;
        }
        sql += " GROUP BY  cu.TABLE_NAME,cu.CONSTRAINT_NAME ORDER BY cu.TABLE_NAME, cu.CONSTRAINT_NAME " ;
        return sql;
    }

    @Override
    public String queryTableViewSQL() {
        return "SELECT VIEW_NAME, TEXT_LENGTH, TEXT FROM USER_VIEWS " ;
    }

    @Override
    public String querySequencesVoSQL() {
        return "SELECT T.SEQUENCE_NAME, T.LAST_NUMBER  FROM USER_SEQUENCES T ORDER BY T.SEQUENCE_NAME " ;
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
        String sql = "SELECT distinct OBJECT_TYPE, OBJECT_NAME FROM user_procedures  " ;
        if (StringUtils.hasText(metaType)){
            sql = sql.concat("WHERE OBJECT_TYPE = '").concat(metaType.toUpperCase()).concat("' ");
        }
        sql = sql.concat("order by OBJECT_TYPE, OBJECT_NAME");
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
     * 查表 或 视图
     * @return
     */
    @Override
    public List<ObTableInfoVO> queryTableInfo(String tableName){
        String sql = "SELECT t1.TABLE_NAME , t2.COMMENTS , t1.STATUS , t1.TEMPORARY ,T2.TABLE_TYPE " +
                "FROM USER_ALL_TABLES t1 join USER_TAB_COMMENTS t2 on t1.TABLE_NAME=t2.TABLE_NAME WHERE T2.TABLE_TYPE='TABLE' ";
//        if (StringUtils.hasText(tableName)){
//            sql += " AND t1.TABLE_NAME = '"+tableName+"' ";
//        }
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
                "            ELSE  TC.DATA_TYPE || '(' || TC.DATA_LENGTH || ')' END  DATA_TYPE,\n" +
                "       CASE TC.NULLABLE WHEN 'N' THEN\n" +
                "           ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  'NOT NULL'\n" +
                "                ELSE ('DEFAULT '|| TC.DATA_DEFAULT || ' NOT NULL') END )\n" +
                "           ELSE ( CASE WHEN  TC.DATA_DEFAULT IS NULL  THEN  ''\n" +
                "                ELSE ('DEFAULT '|| TC.DATA_DEFAULT || '') END) END EXTEND,\n" +
                "       TC.NULLABLE, TC.DATA_DEFAULT,\n" +
                "       TC.COLUMN_ID\n" +
                "FROM USER_TAB_COLUMNS TC   " ;//+
                //"WHERE TABLE_NAME ='AMS_ACCOUNTCHECK1_TD'\n" +
                //"ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        if (StringUtils.hasText(tableName)){
            sql += "WHERE TC.TABLE_NAME = '"+tableName+"' ";
        }
        sql += "ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        return jdbcTemplateService.queryForList(sql, ObTableColumnFullVO.class);
    }



    /**
     * 查表对应的分区数
     * @return
     */
    @Override
    public List<ObTablePartitionVO> queryTablePartitionVO(){
        String sql = "SELECT TABLE_NAME, COUNT(*) COUNT FROM SYS.USER_TAB_PARTITIONS GROUP BY TABLE_NAME" ;
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


    public void executeSQL(){
        String sql = "CREATE OR REPLACE PROCEDURE ALL_TABLE_STATISTICS_COUNT\n" +
                "IS\n" +
                "V_SQL VARCHAR2(1000) DEFAULT '';\n" +
                "BEGIN\n" +
                "\n" +
                "FOR RS IN ( SELECT T.TABLE_NAME FROM USER_TABLES T )LOOP\n" +
                "V_SQL :='ANALYZE TABLE '||RS.TABLE_NAME||' COMPUTE STATISTICS';\n" +
                "EXECUTE IMMEDIATE V_SQL;\n" +
                "COMMIT;\n" +
                "END LOOP;\n" +
                "EXCEPTION\n" +
                "WHEN OTHERS THEN\n" +
                "DBMS_OUTPUT.PUT_LINE('ERRM ALL_TABLE_STATISTICS_COUNT:' || SQLERRM);\n" +
                "END;\n" +
                "/\n" +
                "BEGIN\n" +
                "  ALL_TABLE_STATISTICS_COUNT ;\n" +
                "END ;\n" +
                "/\n" +
                "DROP PROCEDURE ALL_TABLE_STATISTICS_COUNT ;";
        System.out.println("PL SQL : \n " +sql);
        jdbcTemplateService.execute(sql);
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
            sql += " AND cu.TABLE_NAME = '"+tableName+"'" ;
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
     * 查 表索引 (包含普通索引和函數索引的表达式)
     * @return
     */
    @Override
    public List<ObTableIndexVO> queryTableIndexVO(String tableName){
        String sql = "SELECT T.TABLE_NAME, T.INDEX_NAME,\n" +
                "       LISTAGG(CASE\n" +
                "                   WHEN INSTR(I.INDEX_TYPE, 'FUNCTION') > 0 THEN NEWCOLUMN_NAME\n" +
                "                   ELSE T.COLUMN_NAME || ' ' || T.DESCEND END, ',')\n" +
                "               WITHIN GROUP (ORDER BY T.TABLE_NAME,T.INDEX_NAME,T.COLUMN_POSITION) AS COLUMN_NAME,\n" +
                "       I.INDEX_TYPE,\n" +
                "       I.UNIQUENESS\n" +
                "FROM (SELECT N.*,\n" +
                "             (CASE\n" +
                "                  WHEN INSTR(N.COLUMN_NAME, 'SYS_NC') > 0 THEN\n" +
                "                      (SELECT INDEX_COLUMN_EXPRESSION(E.TABLE_NAME, E.INDEX_NAME, E.COLUMN_POSITION)\n" +
                "                       FROM USER_IND_EXPRESSIONS E\n" +
                "                       WHERE E.INDEX_NAME = N.INDEX_NAME AND E.COLUMN_POSITION = N.COLUMN_POSITION)\n" +
                "                  ELSE N.COLUMN_NAME END) AS NEWCOLUMN_NAME\n" +
                "      FROM USER_IND_COLUMNS N\n" ;
        if (StringUtils.hasText(tableName)) {
            sql+= "    WHERE N.TABLE_NAME = 'ACT_RU_CASE_EXECUTION'\n" ;
        }
        sql+= "    ) T, USER_INDEXES I\n" +
                "WHERE T.INDEX_NAME = I.INDEX_NAME\n" +
                "  AND T.TABLE_NAME = I.TABLE_NAME\n" +
                "GROUP BY T.TABLE_NAME, T.INDEX_NAME, I.INDEX_TYPE, I.UNIQUENESS\n" +
                "ORDER BY T.TABLE_NAME, T.INDEX_NAME "  ;
        return jdbcTemplateService.queryForList(sql, ObTableIndexVO.class);
    }

    @Override
    public List<ObTableViewVO> queryTableView() {
        String sql = "SELECT VIEW_NAME, TEXT_LENGTH, TEXT FROM USER_VIEWS " ;
        return jdbcTemplateService.queryForList(sql, ObTableViewVO.class);
    }

    /**
     * 查 PACKAGE/PROCEDURE/FUNCTION
     *
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
    @Override
    public Long queryTableCount(String tableName){
        String sql = "SELECT COUNT(1) FROM  "+tableName;
        return jdbcTemplateService.queryOneColumn(sql, Long.class);
    }


}
