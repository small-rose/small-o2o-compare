package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.core.utils.SmallUtils;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.ObTablePartitionVO;
import com.small.o2o.comp.module.vo.OracleTablePartitionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class MetaTablePartitionService implements MetaBuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public MetaBuzTypeEnum getBuzType() {
        return MetaBuzTypeEnum.META_TAB_PARTITION;
    }

    @Override
    public  List<OracleTablePartitionVO> getCompareMetaList(DsQueryPrams queryPramsVO) {
        return getTablePartitionList();
    }

    /**
     * 表-视图 对比
     *
     * @return
     */
    public List<OracleTablePartitionVO> getTablePartitionList( ) {
        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();

        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

        List<ObTablePartitionVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTablePartitionVO.class);
        List<ObTablePartitionVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTablePartitionVO.class);

        List<String> allTables = new ArrayList<>();
        Map<String, ObTablePartitionVO> obObjMap = new ConcurrentHashMap<>();
        Map<String, ObTablePartitionVO> oracleObjMap = new ConcurrentHashMap<>();

        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjMap = obObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName(), (p) -> p));

            obObjList.forEach(o->allTables.add(o.getTableName()));
        }

        if (!ObjectUtils.isEmpty(oraObjList)) {
            oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getTableName(), Function.identity()));
            obObjList.forEach(o->{
                if (!allTables.contains(o.getTableName())) {
                    allTables.add(o.getTableName());
                }
            });
        }

        List<OracleTablePartitionVO> resultList = new ArrayList<>();
        OracleTablePartitionVO object = null;
        int indexNo = 1;
        log.info("getTablePartitionList 取并集大小为：" + allTables.size());
        for (String n : allTables) {
            object = new OracleTablePartitionVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            ObTablePartitionVO obTiv = obObjMap.get(n);
            ObTablePartitionVO oracleTiv = oracleObjMap.get(n);

            if (indexNo==1){
                object.setCount1(String.valueOf(obObjList.size()));
                object.setCount2(String.valueOf(oraObjList.size()));
            }
            if (SmallUtils.isNotEmpty(obTiv)) {
                ObTablePartitionVO ob = obTiv;
                log.info(ob.toString());
                object.setTableName(ob.getTableName());
                object.setPartType(ob.getPartType());
                object.setPartColumnName(ob.getPartColumnName());
                object.setSubpartType(ob.getSubpartType());
                object.setSubpartColumnName(ob.getSubpartColumnName());
                object.setPartitionCount(ob.getPartitionCount());
            }
            if (SmallUtils.isNotEmpty(oracleTiv)) {
                ObTablePartitionVO oracle = oracleTiv;
                object.setTableName2(oracle.getTableName());
                object.setPartType2(oracle.getPartType());
                object.setPartColumnName2(oracle.getPartColumnName());
                object.setSubpartType2(oracle.getSubpartType());
                object.setSubpartColumnName2(oracle.getSubpartColumnName());
                object.setPartitionCount2(oracle.getPartitionCount());
            }
            resultList.add(object);
            indexNo++;
        }

        return resultList;
    }


}
