package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.vo.ObObjectInfoVO;
import com.small.o2o.comp.module.vo.OracleObjectInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
@Slf4j
@Service
public class MetaObjectListService implements MetaBuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;
    @Override
    public MetaBuzTypeEnum getBuzType() {
        return  MetaBuzTypeEnum.META_OBJECT;
    }

    @Override
    public List<OracleObjectInfoVO> getCompareMetaList(DsQueryPrams queryPramsVO) {
        return getObjectInfo();
    }

    public List<OracleObjectInfoVO> getObjectInfo() {

        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();

        //DynamicDSContextHolder.setDataSourceType(dscVO.getDsFirst());
        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        //List<ObObjectInfoVO> objectVOList = queryMetaService.getObjectInfo(queryPramsVO);
        List<ObObjectInfoVO> objectVOList = queryMetaService.queryObjectList(queryPramsVO, ObObjectInfoVO.class);
        //DynamicDSContextHolder.removeDataSourceType();

        //DynamicDSContextHolder.setDataSourceType(dscVO.getDsSecond());
        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();
        List<ObObjectInfoVO> objectVOList2 = queryMetaService.queryObjectList(queryPramsVO2, ObObjectInfoVO.class);
        //DynamicDSContextHolder.removeDataSourceType();


        List<String> allNames = new ArrayList<>();
        if (!ObjectUtils.isEmpty(objectVOList)) {
            objectVOList.forEach(p -> allNames.add(p.getObjectType()));
        }
        if (!ObjectUtils.isEmpty(objectVOList2)) {
            objectVOList2.forEach(p -> allNames.add(p.getObjectType()));
        }

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

            List<ObObjectInfoVO> obList = objectVOList.stream().parallel().filter(p -> p.getObjectType().equals(n)).collect(Collectors.toList());

            List<ObObjectInfoVO> oraList = objectVOList2.stream().parallel().filter(p -> p.getObjectType().equals(n)).collect(Collectors.toList());
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

        if (ObjectUtils.isEmpty(joinNames)){
            object = new OracleObjectInfoVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            object.setObjectType("数据源"+dscVO.getDsFirst()+"没有"+getBuzType());
            object.setObjectType2("数据源"+dscVO.getDsSecond()+"没有"+getBuzType());
            resultList.add(object);
        }
        return resultList;
    }


}
