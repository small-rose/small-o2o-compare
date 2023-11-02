package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.compare.FilePickService;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTableIndexVO;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.OracleTableIndexVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
public class TableIndexService  implements BuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;


    @Override
    public String getBuzType() {
        return O2OConstants.MetaBuzTypeEnum.TABLE_INDEX.getCode();
    }

    @Override
    public  List getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz) {
        return getTableIndexs(queryPramsVO.getTableName());
    }



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

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();
        if (StringUtils.hasText(tabName)){
            queryPramsVO.setTableName(tabName);
            queryPramsVO2.setTableName(tabName);
        }
        if (StringUtils.hasText(tabName)){
            tableList.add(tabName);
        }else{
            List<ObTableInfoVO> allTableList = MetaDataContextHolder.getAllTableList();
            if (CollectionUtils.isEmpty(allTableList)){
                List<String> tempList = new ArrayList<>();
                //重新查询
                List<ObTableInfoVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableInfoVO.class);
                if (!ObjectUtils.isEmpty(obObjList)) {
                    obObjList.forEach(t -> tempList.add(t.getTableName()));
                }
                List<ObTableInfoVO> obObjList2 = queryMetaService.queryObjectList(queryPramsVO2, ObTableInfoVO.class);;
                if (!ObjectUtils.isEmpty(obObjList)) {
                    obObjList2.forEach(t-> tempList.add(t.getTableName()));
                }
                tableList.addAll(tempList.stream().distinct().collect(Collectors.toList()));

            }else {
                for (ObTableInfoVO tableInfoVO : allTableList) {
                    tableList.add(tableInfoVO.getTableName());
                }
            }
        }
        /*
        List<IndexExpressions> obieList = queryMetaService.queryTableIndexExpressions(queryPramsVO);
        List<IndexExpressions> oraieList = queryMetaService.queryTableIndexExpressions(queryPramsVO2);
        Map<String, String> obFunIndexMap = null;
        Map<String, String> oracleFunIndexMap = null;
        if (!ObjectUtils.isEmpty(obieList) ){
            obFunIndexMap = obieList.stream().collect(
                    Collectors.toMap(IndexExpressions::getIndexName, IndexExpressions::getColumnExpression));
        }
        if (!ObjectUtils.isEmpty(oraieList)){
            oracleFunIndexMap = oraieList.stream().collect(
                    Collectors.toMap(IndexExpressions::getIndexName, IndexExpressions::getColumnExpression));
        }
        */
        List<ObTableIndexVO> obIndexList = queryMetaService.queryObjectList(queryPramsVO, ObTableIndexVO.class);
        List<ObTableIndexVO> oraIndexList = queryMetaService.queryObjectList(queryPramsVO2, ObTableIndexVO.class);
        ConcurrentMap<String, List<ObTableIndexVO>> tabLeftMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, List<ObTableIndexVO>> tabRightMap = new ConcurrentHashMap<>();
        Set<String> allTableSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(obIndexList)) {
            tabLeftMap = obIndexList.parallelStream().collect(Collectors.groupingByConcurrent(ObTableIndexVO::getTableName));
            allTableSet.addAll(tabLeftMap.keySet());
        }
        if (!ObjectUtils.isEmpty(oraIndexList)) {
            tabRightMap = oraIndexList.parallelStream().collect(Collectors.groupingByConcurrent(ObTableIndexVO::getTableName));
            allTableSet.addAll(tabRightMap.keySet());
        }
        int i = 0 ;
        Map<String, ObTableIndexVO> obObjMap = new HashMap<>();
        Map<String, ObTableIndexVO> oracleObjMap = new HashMap<>();
        OracleTableIndexVO object = null;
        for (String tableName : allTableSet) {

            List<ObTableIndexVO> obObjList = tabLeftMap.get(tableName);
            List<ObTableIndexVO> oraObjList = tabRightMap.get(tableName);

            System.out.println(i+" Table "+tableName+"  ob indexs " +obObjList.size() +" oracle indexs " +oraObjList.size());
            i++;
            if (ObjectUtils.isEmpty(obObjList) && ObjectUtils.isEmpty(oraObjList)){
                continue;
            }
            List<String> allIndexs = new ArrayList<>();

            if(!ObjectUtils.isEmpty(obObjList)) {
                for (ObTableIndexVO p : oraObjList) {
                    if (!allIndexs.contains(p.getIndexName())) {
                        allIndexs.add(p.getIndexName());
                    }
                }
                obObjMap = obObjList.stream().collect(
                        Collectors.toMap(o -> o.getIndexName(), (p) -> p));
            }
            if(!ObjectUtils.isEmpty(obObjList)) {
                for (ObTableIndexVO p : obObjList) {
                    if (!allIndexs.contains(p.getIndexName())) {
                        allIndexs.add(p.getIndexName());
                    }
                }
                oracleObjMap = oraObjList.stream().collect(
                        Collectors.toMap(o -> o.getIndexName(), Function.identity()));
            }

            int indexNo = 1;
            for (String n : allIndexs) {
                object = new OracleTableIndexVO();
                object.setNo(String.valueOf(indexNo));
                object.setNo2(String.valueOf(indexNo));

                ObTableIndexVO tmpob = obObjMap.get(n);
                ObTableIndexVO tmpora = oracleObjMap.get(n);
                if (!ObjectUtils.isEmpty(tmpob)) {
                    ObTableIndexVO ob = tmpob;
                    object.setTableName(ob.getTableName());
                    object.setIndexName(ob.getIndexName());
                    object.setColumnName(ob.getColumnName());
                    object.setIndexType(ob.getIndexType());
                    object.setUniqueness(ob.getUniqueness());
                }
                if (!ObjectUtils.isEmpty(tmpora)) {
                    ObTableIndexVO oracle = tmpora;
                    object.setTableName2(oracle.getTableName());
                    object.setIndexName2(oracle.getIndexName());
                    object.setColumnName2(oracle.getColumnName());
                    object.setIndexType2(oracle.getIndexType());
                    object.setUniqueness2(oracle.getUniqueness());
                }

                // TODO 生成索引建议的特殊处理
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

        if (!ObjectUtils.isEmpty(ddlList)){
            System.out.println("生成修复索引DDL建议");
            ddlList.forEach(System.out::println);
        }
        if (ObjectUtils.isEmpty(tableList)) {
            OracleTableIndexVO indexVO = new OracleTableIndexVO();
            indexVO.setTableName("未发现表");
            indexVO.setTableName2("未发现表");
            resultList.add(indexVO);
        }
        //String path = "E:\\obgenerator\\ORA_DDL_INDEX.SQL";
        //FileRWUtils.fileWriter(path, ddlList);
        return resultList;
    }


}
