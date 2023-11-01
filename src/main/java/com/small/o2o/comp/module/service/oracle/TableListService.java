package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.ObTablePartitionVO;
import com.small.o2o.comp.module.vo.ObTableViewVO;
import com.small.o2o.comp.module.vo.OracleTableInfoVO;
import com.small.o2o.comp.module.vo.OracleTableViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class TableListService  implements BuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public String getBuzType() {
        return O2OConstants.MetaBuzTypeEnum.TABLE_INFO.getCode();
    }

    @Override
    public  List getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz) {
        return getTableInfo();
    }

    /**
     * 表-视图 对比
     *
     * @return
     */
    public List<OracleTableInfoVO> getTableInfo( ) {
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

        List<ObTableInfoVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableInfoVO.class);
        List<ObTableInfoVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTableInfoVO.class);

        Map<String, ObTableInfoVO> obObjMap = null;
        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjMap = obObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName(), (p) -> p));
        }
        Map<String, ObTableInfoVO> oracleObjMap = null;
        if (!ObjectUtils.isEmpty(oraObjList)) {
            oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName(), Function.identity()));
        }
        //OB 查分区数
        List<ObTablePartitionVO> obTablePartitionList = queryMetaService.queryTablePartitionVO(queryPramsVO);
        Map<String, Long> obPartMap = null;
        if (!ObjectUtils.isEmpty(obTablePartitionList)) {
            obPartMap = obTablePartitionList.stream().collect(Collectors.toMap(ObTablePartitionVO::getTableName, ObTablePartitionVO::getCount));
        }
        //OB 查记录数
        List<ObTablePartitionVO> obRecordList = queryMetaService.queryTableReCords(queryPramsVO);
        Map<String, Long> obRecordMap = null;
        if (!ObjectUtils.isEmpty(obRecordList)) {
            obRecordMap = obRecordList.stream().collect(Collectors.toMap(ObTablePartitionVO::getTableName, ObTablePartitionVO::getCount));
        }

        //ORACLE 查分区数
        List<ObTablePartitionVO> oraTablePartitionList = queryMetaService.queryTablePartitionVO(queryPramsVO2);
        Map<String, Long> oraPartMap = null ;
        if (!ObjectUtils.isEmpty(oraTablePartitionList)) {
            oraPartMap = oraTablePartitionList.stream().collect(Collectors.toMap(ObTablePartitionVO::getTableName, ObTablePartitionVO::getCount));
        }
        //ORACLE 查记录数
        List<ObTablePartitionVO> oraRecordList = queryMetaService.queryTableReCords(queryPramsVO2);
        //Map<String, Long> oraRecordMap = oraRecordList.stream().collect(Collectors.toMap(ObTablePartitionVO::getTableName, ObTablePartitionVO::getCount));
        Map<String, Long> oraRecordMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(oraTablePartitionList)) {
            oraRecordMap = oraRecordList.stream().collect(Collectors.toMap(ObTablePartitionVO::getTableName, ObTablePartitionVO::getCount));
        }
        List<String> allTables = new ArrayList<>();
        if (!ObjectUtils.isEmpty(obObjList)) {
            for (ObTableInfoVO obTableInfoVO : obObjList) {
                allTables.add(obTableInfoVO.getTableName());

            }
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            for (ObTableInfoVO obTableInfoVO : oraObjList) {
                if (!allTables.contains(obTableInfoVO.getTableName())) {
                    allTables.add(obTableInfoVO.getTableName());
                }
            }
        }
        List<ObTableInfoVO> cache = new ArrayList<>();
        if (!ObjectUtils.isEmpty(obObjList)) {
            cache.addAll(obObjList);
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            cache.addAll(oraObjList);
        }
        MetaDataContextHolder.setAllTableList(cache);

        List<OracleTableInfoVO> resultList = new ArrayList<>();
        OracleTableInfoVO object = null;
        int indexNo = 1;
        log.info("getTableInfo 取并集大小为：" + allTables.size());
        for (String n : allTables) {
            object = new OracleTableInfoVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            ObTableInfoVO obTiv = obObjMap.get(n);
            ObTableInfoVO oracleTiv = oracleObjMap.get(n);

            if (obTiv != null) {
                ObTableInfoVO ob = obTiv;
                log.info(ob.toString());
                object.setTableName(ob.getTableName());
                object.setTableComment(ob.getTableComment());
                if (obPartMap.get(object.getTableName()) != null) {
                    object.setPartitions(obPartMap.get(object.getTableName()));
                }
                if (obRecordMap.get(object.getTableName()) != null) {
                    object.setCountRows(obRecordMap.get(object.getTableName()));
                }

            }
            if (oracleTiv != null ) {
                ObTableInfoVO oracle = oracleTiv;
                object.setTableName2(oracle.getTableName());
                object.setTableComment2(oracle.getTableComment());

                if (oraPartMap.get(object.getTableName2()) != null) {
                    object.setPartitions2(obPartMap.get(object.getTableName2()));
                }
                if (oraRecordMap.get(object.getTableName2()) != null) {
                    object.setCountRows2(obRecordMap.get(object.getTableName2()));
                }
            }
            resultList.add(object);
            indexNo++;
        }

        return resultList;
    }

    /**
     *  视图查询比较结果
     * @return
     */
    public List<OracleTableViewVO> getTableViewList() {
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

        List<ObTableViewVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableViewVO.class);
        List<ObTableViewVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTableViewVO.class);

        Map<String, ObTableViewVO> obObjMap = null;
        Map<String, ObTableViewVO> oracleObjMap = null;
        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjMap = obObjList.stream().collect(
                    Collectors.toMap(ObTableViewVO::getViewName, e->e ,(oldVal, newVal) -> oldVal));
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getViewName(), Function.identity(),(oldVal, newVal) -> oldVal));
        }


        List<String> allTables = new ArrayList<>();
        if (!ObjectUtils.isEmpty(obObjList)) {
            for (ObTableViewVO obTableInfoVO : obObjList) {
                allTables.add(obTableInfoVO.getViewName());

            }
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            for (ObTableViewVO obTableInfoVO : oraObjList) {
                if (!allTables.contains(obTableInfoVO.getViewName())) {
                    allTables.add(obTableInfoVO.getViewName());
                }
            }
        }


        List<OracleTableViewVO> resultList = new ArrayList<>();
        OracleTableViewVO object = null;
        int indexNo = 1;
        log.info("getTableInfo 取并集大小为：" + allTables.size());
        for (String n : allTables) {
            object = new OracleTableViewVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            ObTableViewVO obTiv = obObjMap.get(n);
            ObTableViewVO oracleTiv = oracleObjMap.get(n);

            if (obTiv != null) {
                ObTableViewVO ob = obTiv;
                object.setViewName(ob.getViewName());
                object.setTextLength(ob.getTextLength());
                object.setText(ob.getText());
            }
            if (oracleTiv != null ) {
                ObTableViewVO oracle = oracleTiv;
                object.setViewName2(oracle.getViewName());
                object.setTextLength2(oracle.getTextLength());
                object.setText2(oracle.getText());
            }
            resultList.add(object);
            indexNo++;
        }
        return resultList;
    }
}
