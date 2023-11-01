package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.ObTablePrimaryKeyVO;
import com.small.o2o.comp.module.vo.OracleTablePrimaryKeyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
public class TablePrimaryKeyService  implements BuzTypeService {


    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public String getBuzType() {
        return O2OConstants.MetaBuzTypeEnum.TABLE_PRIMARYKEY.getCode();
    }

    @Override
    public  List getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz) {
        return getTablePrimaryKey(queryPramsVO.getTableName());
    }

    String DROP_DDL = "ALTER TABLE %s DROP CONSTRAINT %S;";
    String ADD_DDL = "ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY( %s );";


    /**
     * 表索引对比
     *
     * @return
     */
    public List<OracleTablePrimaryKeyVO> getTablePrimaryKey(String tabName) {

        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();
        List<OracleTablePrimaryKeyVO> resultList = new ArrayList<>();

        List<String> tableList = new ArrayList<>();
        List<String> ddlList = new ArrayList<>();

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).tableName(tabName).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).tableName(tabName).build();
        List<ObTableInfoVO> allTableList = MetaDataContextHolder.getAllTableList();
        if (CollectionUtils.isEmpty(allTableList)){
            List<String> tempList = new ArrayList<>();
            //重新查询
            List<ObTableInfoVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableInfoVO.class);
            if (!ObjectUtils.isEmpty(obObjList)) {
                obObjList.forEach(t -> tempList.add(t.getTableName()));
            }
            List<ObTableInfoVO> obObjList2 = queryMetaService.queryObjectList(queryPramsVO2, ObTableInfoVO.class);
            if (!ObjectUtils.isEmpty(obObjList2)) {
                obObjList2.forEach(t -> tempList.add(t.getTableName()));
            }
            tableList = tempList.stream().distinct().collect(Collectors.toList());

        }else{
            List<ObTableInfoVO> obObjList2 = queryMetaService.queryObjectList(queryPramsVO, ObTableInfoVO.class);
            if (!ObjectUtils.isEmpty(obObjList2)) {
                for (ObTableInfoVO tableInfoVO : obObjList2) {
                    tableList.add(tableInfoVO.getTableName());
                }
            }
        }

        int i = 0 ;
        for (String tableName : tableList) {

            queryPramsVO.setTableName(tableName);
            List<ObTablePrimaryKeyVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTablePrimaryKeyVO.class);
            queryPramsVO2.setTableName(tableName);
            List<ObTablePrimaryKeyVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTablePrimaryKeyVO.class);
            System.out.println(i+" Table primary key  "+tableName+"  ob primarykey " +obObjList.size() +" oracle primarykey " +oraObjList.size());
            i++;
            if (ObjectUtils.isEmpty(obObjList) && ObjectUtils.isEmpty(oraObjList) ){
                continue;
            }
            List<String> allIndexs = new ArrayList<>();

            Map<String, ObTablePrimaryKeyVO> obObjMap = obObjList.stream().collect(
                    Collectors.toMap(ObTablePrimaryKeyVO::getConstraintName, (p) -> p));

            Map<String, ObTablePrimaryKeyVO> oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getConstraintName(), Function.identity()));

            for (ObTablePrimaryKeyVO p : oraObjList) {
                if (!allIndexs.contains(p.getConstraintName())) {
                    allIndexs.add(p.getConstraintName());
                }
            }
            for (ObTablePrimaryKeyVO p : obObjList) {
                if (!allIndexs.contains(p.getConstraintName())) {
                    allIndexs.add(p.getConstraintName());
                }
            }

            OracleTablePrimaryKeyVO object = null;
            int indexNo = 1;
            for (String n : allIndexs) {
                object = new OracleTablePrimaryKeyVO();
                object.setNo(String.valueOf(indexNo));
                object.setNo2(String.valueOf(indexNo));

                ObTablePrimaryKeyVO tmpob = obObjMap.get(n);
                ObTablePrimaryKeyVO tmpora = oracleObjMap.get(n);
                //if (obList.size() > 0){
                if (tmpob != null) {
                    ObTablePrimaryKeyVO ob = tmpob;
                    object.setTableName(ob.getTableName());
                    object.setConstraintName(ob.getConstraintName());
                    object.setColumnName(ob.getColumnName());

                }
                if (tmpora != null) {
                    ObTablePrimaryKeyVO oracle = tmpora;
                    object.setTableName2(oracle.getTableName());
                    object.setConstraintName2(oracle.getConstraintName());
                    object.setColumnName2(oracle.getColumnName());
                }

                // ORACLE 有， OB 沒有，--有能是名字不一样
                if (tmpora!=null && tmpob==null){
                    String ddl = String.format(DROP_DDL, tmpora.getTableName(), tmpora.getConstraintName());
                    ddlList.add(ddl);
                    object.setSql(ddl);
                }
                // ORACLE 没有有， OB 有，--有能是名字不一样，直接修改主键名称
                if (tmpora==null && tmpob!=null){
                    String ddl = String.format(ADD_DDL, tmpob.getTableName(), tmpob.getConstraintName(), tmpob.getColumnName());
                    ddlList.add(ddl);
                    object.setSql(ddl);
                }

                if (tmpora!=null && tmpob!=null){
                    if (tmpob.getTableName().equals(tmpora.getTableName()) &&
                    tmpob.getConstraintName().equals(tmpora.getConstraintName()) &&
                    !tmpob.getColumnName().equals(tmpora.getColumnName())){
                        String ddld = String.format(DROP_DDL, tmpora.getTableName(), tmpora.getConstraintName());
                        ddlList.add(ddld);
                        String ddla = String.format(ADD_DDL, tmpob.getTableName(), tmpob.getConstraintName(), tmpob.getColumnName());
                        ddlList.add(ddla);
                        object.setSql(ddld + ddla);
                    }

                }
                resultList.add(object);
                indexNo++;
            }
            allIndexs.clear();
        }

        //String path = "E:\\obgenerator\\ORA_DDL_PRIMARY_KEY.SQL";
        //FileRWUtils.fileWriter(path, ddlList);
        return resultList;
    }


}
