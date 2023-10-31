package com.small.o2o.comp.module.service.metadata;


import com.small.o2o.comp.config.datasource.DynamicDSContextHolder;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObObjectInfoVO;
import com.small.o2o.comp.module.vo.OracleObjectInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaocai
 */
@Slf4j
@Service
public class ObjectInfoService {

    @Autowired
    private QueryMetaService queryMetaService;


    public List<OracleObjectInfoVO> getObjectInfo() {

        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        DynamicDSContextHolder.setDataSourceType(dscVO.getDsFirst());
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).build();
        //List<ObObjectInfoVO> typesVOList = queryMetaService.getObjectInfo(queryPramsVO);
        List<ObObjectInfoVO> typesVOList = queryMetaService.getObjectList(queryPramsVO, ObObjectInfoVO.class, O2OConstants.SQL_TYPE);
        DynamicDSContextHolder.removeDataSourceType();

        DynamicDSContextHolder.setDataSourceType(dscVO.getDsSecond());
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).build();
        List<ObObjectInfoVO> typesVOList2 = queryMetaService.getObjectInfo(queryPramsVO2);
        DynamicDSContextHolder.removeDataSourceType();


        List<String> allNames = new ArrayList<>();
        typesVOList.forEach(p -> allNames.add(p.getObjectType()));
        typesVOList2.forEach(p -> allNames.add(p.getObjectType()));

        //去重，获取并集 对象 新集合
        List<String> joinNames = allNames.stream().distinct().collect(Collectors.toList());

        List<OracleObjectInfoVO> resultList = new ArrayList<>();
        OracleObjectInfoVO object = null;
        int indexNo = 1;
        log.info("getProcedureList 取并集大小为：" + joinNames.size());

        for (String n : joinNames) {
            object = new OracleObjectInfoVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));

            List<ObObjectInfoVO> obList = typesVOList.stream().parallel().filter(p -> p.getObjectType().equals(n)).collect(Collectors.toList());

            List<ObObjectInfoVO> oraList = typesVOList2.stream().parallel().filter(p -> p.getObjectType().equals(n)).collect(Collectors.toList());
            if (obList.size() > 0) {
                ObObjectInfoVO ob = obList.get(0);
                object.setObjectType(ob.getObjectType());
                object.setCount(ob.getCount());
            }
            if (oraList.size() > 0) {
                ObObjectInfoVO oracle = oraList.get(0);
                object.setObjectType2(oracle.getObjectType());
                object.setCount2(oracle.getCount());
            }
            resultList.add(object);
            indexNo++;
        }
        ;
        return resultList;
    }

}
