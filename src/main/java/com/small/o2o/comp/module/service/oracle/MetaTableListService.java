package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import com.small.o2o.comp.module.vo.ObTablePartitionVO;
import com.small.o2o.comp.module.vo.OracleTableInfoVO;
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
public class MetaTableListService implements MetaBuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public MetaBuzTypeEnum getBuzType() {
        return MetaBuzTypeEnum.META_TABLE;
    }

    @Override
    public  List getCompareMetaList(DsQueryPrams queryPramsVO) {
        return getTableInfo();
    }

    /**
     * 表-视图 对比
     *
     * @return
     */
    public List<OracleTableInfoVO> getTableInfo( ) {
        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();

        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

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


}
