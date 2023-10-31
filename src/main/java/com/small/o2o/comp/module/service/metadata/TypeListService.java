package com.small.o2o.comp.module.service.metadata;

import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTypesVO;
import com.small.o2o.comp.module.vo.OracleTypesVO;
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
public class TypeListService {

    @Autowired
    private QueryMetaService queryMetaService;



    public List<OracleTypesVO> getTypeList() {

        DSCompareVO dsCompare = MetaDataContextHolder.getDsCompare();
        ;
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dsCompare.getDsFirst()).metaType("").build();
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dsCompare.getDsSecond()).metaType("").build();
        List<ObTypesVO> typesVOList = queryMetaService.queryTypesVO(queryPramsVO);
        List<ObTypesVO> typesVOList2 = queryMetaService.queryTypesVO(queryPramsVO2);

        List<String> allNames = new ArrayList<>();
        typesVOList.stream().forEach(p -> allNames.add(p.getTypeName()));
        typesVOList2.stream().forEach(p -> allNames.add(p.getTypeName()));

        //去重，获取并集 对象 新集合
        List<String> joinNames = allNames.stream().distinct().collect(Collectors.toList());

        List<OracleTypesVO> resultList = new ArrayList<>();
        OracleTypesVO object = null;
        int indexNo = 1;
        log.info("getProcedureList 取并集大小为：" + joinNames.size());

        for (String n : joinNames) {
            object = new OracleTypesVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            if (indexNo == 1) {
                object.setCount1(String.valueOf(typesVOList.size()));
                object.setCount2(String.valueOf(typesVOList2.size()));
            }
            List<ObTypesVO> obList = typesVOList.stream().parallel().filter(p -> p.getTypeName().equals(n)).collect(Collectors.toList());

            List<ObTypesVO> oraList = typesVOList2.stream().parallel().filter(p -> p.getTypeName().equals(n)).collect(Collectors.toList());
            if (obList.size() > 0) {
                ObTypesVO ob = obList.get(0);
                object.setTypeName(ob.getTypeName());
                object.setTypecode(ob.getTypecode());
            }
            if (oraList.size() > 0) {
                ObTypesVO oracle = oraList.get(0);
                object.setTypeName(oracle.getTypeName());
                object.setTypecode(oracle.getTypecode());
            }
            resultList.add(object);
            indexNo++;
        }
        ;
        return resultList;
    }



}
