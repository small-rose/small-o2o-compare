package com.small.o2o.comp.module.service.metadata;


import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.facade.FilePickService;
import com.small.o2o.comp.core.utils.FileRWUtils;
import com.small.o2o.comp.module.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaocai
 */
@Slf4j
@Service
public class TableIndexService {

    @Autowired
    private QueryMetaService queryMetaService;


    private FilePickService filePickService;

    String DROP_DDL = "DROP INDEX %s  ;";
    String ADD_DDL = "CREATE %s INDEX %s ON %s ( %s ) tablespace AMS_DATA  pctfree 10  initrans 2 maxtrans 255 storage (  initial 64K next 1M minextents 1 maxextents unlimited );";
    /**
     * 表索引对比
     *
     * @return
     */
    public List<OracleTableIndexVO> getTableIndexs(String tabName) {

        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();
        List<String> ddlList = new ArrayList<>();
        List<OracleTableIndexVO> resultList = new ArrayList<>();

        List<String> tableList = new ArrayList<>();

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).tableName(tabName).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).tableName(tabName).build();
        List<ObTableInfoVO> allTableList = MetaDataContextHolder.getAllTableList();
        if (CollectionUtils.isEmpty(allTableList)){
             List<String> tempList = new ArrayList<>();
            //重新查询

            List<ObTableInfoVO> obObjList = queryMetaService.queryTableInfo(queryPramsVO);
            obObjList.forEach(t-> tempList.add(t.getTableName()));

            List<ObTableInfoVO> obObjList2 = queryMetaService.queryTableInfo(queryPramsVO2);;
            obObjList2.forEach(t-> tempList.add(t.getTableName()));

            tableList = tempList.stream().distinct().collect(Collectors.toList());

        }else{
             List<ObTableInfoVO> obObjList2 = queryMetaService.queryTableInfo(queryPramsVO);;
            for (ObTableInfoVO tableInfoVO : obObjList2) {
                tableList.add(tableInfoVO.getTableName());
            }
        }
        List<IndexExpressions> obieList = queryMetaService.queryTableIndexExpressions(queryPramsVO);
        List<IndexExpressions> oraieList = queryMetaService.queryTableIndexExpressions(queryPramsVO2);

        Map<String, String> obFunIndexMap = obieList.stream().collect(
                Collectors.toMap(IndexExpressions::getIndexName, IndexExpressions::getColumnExpression));
        Map<String, String> oracleFunIndexMap = oraieList.stream().collect(
                Collectors.toMap(IndexExpressions::getIndexName, IndexExpressions::getColumnExpression));

        int i = 0 ;
        for (String tableName : tableList) {

            queryPramsVO.setTableName(tableName);
            List<ObTableIndexVO> obObjList = queryMetaService.queryTableIndexVO(queryPramsVO);
            queryPramsVO2.setTableName(tableName);
            List<ObTableIndexVO> oraObjList = queryMetaService.queryTableIndexVO(queryPramsVO2);

            System.out.println(i+" Table "+tableName+"  ob indexs " +obObjList.size() +" oracle indexs " +oraObjList.size());
            i++;
            if (obObjList.size()==0 && oraObjList.size()==0){
                continue;
            }
            List<String> allIndexs = new ArrayList<>();

            Map<String, ObTableIndexVO> obObjMap = obObjList.stream().collect(
                    Collectors.toMap(o -> o.getIndexName(), (p) -> p));

            Map<String, ObTableIndexVO> oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getIndexName(), Function.identity()));

            for (ObTableIndexVO p : oraObjList) {
                if (!allIndexs.contains(p.getIndexName())) {
                    allIndexs.add(p.getIndexName());
                }
            }
            for (ObTableIndexVO p : obObjList) {
                if (!allIndexs.contains(p.getIndexName())) {
                    allIndexs.add(p.getIndexName());
                }
            }


            OracleTableIndexVO object = null;
            int indexNo = 1;
            for (String n : allIndexs) {
                object = new OracleTableIndexVO();
                object.setNo(String.valueOf(indexNo));
                object.setNo2(String.valueOf(indexNo));

                ObTableIndexVO tmpob = obObjMap.get(n);
                ObTableIndexVO tmpora = oracleObjMap.get(n);
                //if (obList.size() > 0){
                if (tmpob != null) {
                    ObTableIndexVO ob = tmpob;
                    object.setTableName(ob.getTableName());
                    object.setIndexName(ob.getIndexName());
                    object.setColumnName(ob.getColumnName());
                    if (ob.getIndexType()!=null && ob.getIndexType().startsWith("FUNCTION-")){
                        String columnName = obFunIndexMap.get(ob.getIndexName());
                        String result = ob.getColumnName().replaceAll("SYS_NC\\d+\\$", columnName);
                        object.setColumnName(result);
                    }
                    object.setIndexType(ob.getIndexType());
                    object.setUniqueness(ob.getUniqueness());
                }
                if (tmpora != null) {
                    ObTableIndexVO oracle = tmpora;
                    object.setTableName2(oracle.getTableName());
                    object.setIndexName2(oracle.getIndexName());
                    object.setColumnName2(oracle.getColumnName());
                    if (oracle.getIndexType()!=null && oracle.getIndexType().startsWith("FUNCTION-")){
                        String columnName = oracleFunIndexMap.get(oracle.getIndexName());
                        String result = object.getColumnName2().replaceAll("SYS_NC\\d+\\$", columnName);
                        object.setColumnName2(result);
                    }
                    object.setIndexType2(oracle.getIndexType());
                    object.setUniqueness2(oracle.getUniqueness());
                }


                // ORACLE 有， OB 沒有，--有能是名字不一样
                if (tmpora!=null && tmpob==null){
                    String ddl = String.format(DROP_DDL, tmpora.getIndexName());
                    ddlList.add(ddl);
                    object.setSql(ddl);
                }
                // ORACLE 没有， OB 有，--有能是名字不一样，直接修改主键名称
                if (tmpora==null && tmpob!=null){
                    String unique = "UNIQUE".equals(tmpob.getUniqueness()) ? tmpob.getUniqueness() : "";
                    String ddl = String.format(ADD_DDL, unique, tmpob.getIndexName(), tmpob.getTableName(), object.getColumnName());
                    ddlList.add(ddl);
                    object.setSql(ddl);
                }

                if (tmpora!=null && tmpob!=null){
                    if (tmpob.getTableName().equals(tmpora.getTableName()) &&
                            tmpob.getIndexName().equals(tmpora.getIndexName()) &&
                            !tmpob.getColumnName().equals(tmpora.getColumnName())){
                        String ddld = String.format(DROP_DDL, tmpora.getIndexName());
                        ddlList.add(ddld);
                        String unique = "UNIQUE".equals(tmpora.getUniqueness()) ? tmpora.getUniqueness() : "";
                        String ddla = String.format(ADD_DDL, unique, tmpob.getIndexName(), tmpob.getTableName(), object.getColumnName());
                        ddlList.add(ddla);
                        object.setSql(ddld + ddla);
                    }

                }
                resultList.add(object);
                indexNo++;
            }
            allIndexs.clear();
        }

        String path = "E:\\obgenerator\\ORA_DDL_INDEX.SQL";
        FileRWUtils.fileWriter(path, ddlList);
        return resultList;
    }


}
