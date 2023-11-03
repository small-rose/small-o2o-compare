package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.core.utils.SmallUtils;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.ObTablePrimaryKeyVO;
import com.small.o2o.comp.module.vo.OracleTablePrimaryKeyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/1 029 1:37
 * @version: v1.0
 */
@Slf4j
@Service
public class MetaTablePrimaryKeyService implements MetaBuzTypeService {


    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public MetaBuzTypeEnum getBuzType() {
        return  MetaBuzTypeEnum.META_TAB_PRIMARY_KEY;
    }

    @Override
    public  List getCompareMetaList(DsQueryPrams queryPramsVO) {
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

        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();
        List<OracleTablePrimaryKeyVO> resultList = new ArrayList<>();

        List<String> tableList = new ArrayList<>();
        List<String> ddlList = new ArrayList<>();

        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

        if (StringUtils.hasText(tabName)){
            tableList.add(tabName);
            queryPramsVO.setTableName(tabName);
            queryPramsVO2.setTableName(tabName);
        }else{
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
                for (ObTableInfoVO tableInfoVO : allTableList) {
                    tableList.add(tableInfoVO.getTableName());
                }
            }
        }

        List<ObTablePrimaryKeyVO> obPkList = queryMetaService.queryObjectList(queryPramsVO, ObTablePrimaryKeyVO.class);
        List<ObTablePrimaryKeyVO> oraPkList = queryMetaService.queryObjectList(queryPramsVO2, ObTablePrimaryKeyVO.class);

        ConcurrentMap<String, List<ObTablePrimaryKeyVO>> tabLeftMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, List<ObTablePrimaryKeyVO>> tabRightMap = new ConcurrentHashMap<>();
        Set<String> allTableSet = new HashSet<>();
        if (SmallUtils.isNotEmpty(obPkList)){
            tabLeftMap = obPkList.parallelStream().collect(Collectors.groupingByConcurrent(ObTablePrimaryKeyVO::getTableName));
            allTableSet.addAll(tabLeftMap.keySet());
        }
        if (SmallUtils.isNotEmpty(oraPkList)){
            tabRightMap = oraPkList.parallelStream().collect(Collectors.groupingByConcurrent(ObTablePrimaryKeyVO::getTableName));
            allTableSet.addAll(tabRightMap.keySet());
        }

        int i = 0 ;
        Map<String, ObTablePrimaryKeyVO> obObjMap = null;
        Map<String, ObTablePrimaryKeyVO> oracleObjMap = null;
        for (String tableName : allTableSet) {

            List<ObTablePrimaryKeyVO> leftList  = tabLeftMap.get(tableName);
            List<ObTablePrimaryKeyVO> rightList  = tabRightMap.get(tableName);
             i++;
            if (SmallUtils.isEmpty(leftList) && SmallUtils.isEmpty(rightList) ){
                continue;
            }
            List<String> allIndexs = new ArrayList<>();

            if (SmallUtils.isNotEmpty(leftList)) {
                obObjMap = leftList.stream().collect(
                        Collectors.toMap(ObTablePrimaryKeyVO::getConstraintName, (p) -> p));
                for (ObTablePrimaryKeyVO p : leftList) {
                    if (!allIndexs.contains(p.getConstraintName())) {
                        allIndexs.add(p.getConstraintName());
                    }
                }
            }
            if (SmallUtils.isNotEmpty(rightList)) {
                oracleObjMap = rightList.stream().collect(
                        Collectors.toMap(o -> o.getConstraintName(), Function.identity()));
                for (ObTablePrimaryKeyVO p : rightList) {
                    if (!allIndexs.contains(p.getConstraintName())) {
                        allIndexs.add(p.getConstraintName());
                    }
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
                if (SmallUtils.isNotEmpty(tmpob)) {
                    ObTablePrimaryKeyVO ob = tmpob;
                    object.setTableName(ob.getTableName());
                    object.setConstraintName(ob.getConstraintName());
                    object.setColumnName(ob.getColumnName());

                }
                if (SmallUtils.isNotEmpty(tmpora)) {
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
