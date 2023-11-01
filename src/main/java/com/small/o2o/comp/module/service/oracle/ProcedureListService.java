package com.small.o2o.comp.module.service.oracle;


import cn.hutool.core.util.ObjectUtil;
import com.small.o2o.comp.config.datasource.DynamicDSContextHolder;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.facade.FilePickService;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObProcedureVO;
import com.small.o2o.comp.module.vo.OracleProcedureVO;
import lombok.extern.slf4j.Slf4j;
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
public class ProcedureListService  implements BuzTypeService {


    @Autowired
    private QueryMetaDataService queryMetaService;

    private FilePickService filePickService ;

    @Override
    public String getBuzType() {
        return O2OConstants.MetaBuzTypeEnum.OBJECT_INFO.getCode();
    }

    @Override
    public  List getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz) {
        return getProcedureList(queryPramsVO.getMetaType());
    }

    public List<OracleProcedureVO> getProcedureList(String type) {

        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();
        DynamicDSContextHolder.setDataSourceType(dscVO.getDsFirst());
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().queryType(getBuzType())
                .dataSourceName(dscVO.getDsFirst()).metaType(type).build();
        List<ObProcedureVO> procedureVOS = queryMetaService.queryObjectList(queryPramsVO, ObProcedureVO.class);
        //List<ObProcedureVO> procedureVOS1 = queryMetaService.queryNameListProcedureVO(queryPramsVO);
        DynamicDSContextHolder.removeDataSourceType();

        DynamicDSContextHolder.setDataSourceType(dscVO.getDsSecond());
        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().queryType(getBuzType())
                .dataSourceName(dscVO.getDsSecond()).metaType(type).build();
        List<ObProcedureVO> procedureVOS2 = queryMetaService.queryObjectList(queryPramsVO2, ObProcedureVO.class);
        //List<ObProcedureVO> procedureVOS1 = queryMetaService.queryNameListProcedureVO(queryPramsVO);
        DynamicDSContextHolder.removeDataSourceType();

        List<String> allNames = new ArrayList<>();

        Map<String, ObProcedureVO> procedureVOMap = null;
        Map<String, ObProcedureVO> procedureVOMap2 = null;
        if (!ObjectUtils.isEmpty(procedureVOS)){
            procedureVOS.stream().forEach(p -> allNames.add(p.getObjectName().concat(p.getProcedureName() == null ? "" : p.getProcedureName())));
            procedureVOMap = procedureVOS.stream().collect(
                    Collectors.toMap(p -> p.getObjectName().concat(p.getProcedureName()), (p) -> p));
        }
        if (!ObjectUtils.isEmpty(procedureVOS2)){
            procedureVOS2.stream().forEach(p -> allNames.add(p.getObjectName().concat(p.getProcedureName() == null ? "" : p.getProcedureName())));
            procedureVOMap2 = procedureVOS2.stream().collect(
                    Collectors.toMap(p -> p.getObjectName().concat(p.getProcedureName()), Function.identity()));
        }


        //去重，获取并集 对象 新集合
        List<String> joinNames = allNames.stream().distinct().collect(Collectors.toList());

        List<OracleProcedureVO> resultList = new ArrayList<>();
        OracleProcedureVO procedure = null;
        int indexNo = 1;
        log.info("getProcedureList 取并集大小为：" + joinNames.size());

        for (String n : joinNames) {
            procedure = new OracleProcedureVO();
            procedure.setNo(String.valueOf(indexNo));
            procedure.setNo2(String.valueOf(indexNo));
            if (indexNo == 1) {
                procedure.setCount1(String.valueOf(procedureVOS.size()));
                procedure.setCount2(String.valueOf(procedureVOS2.size()));
            }
            ObProcedureVO obLeft = procedureVOMap.get(n);

            if (ObjectUtil.isNotNull(obLeft)) {
                ObProcedureVO ob = obLeft;
                procedure.setObjectType(ob.getObjectType());
                procedure.setObjectName(ob.getObjectName());
                procedure.setProcedureName(ob.getProcedureName());
            }
            ObProcedureVO obRight = procedureVOMap2.get(n);
            if (ObjectUtil.isNotNull(obRight)) {
                ObProcedureVO oracle = obRight ;
                procedure.setObjectType2(oracle.getObjectType());
                procedure.setObjectName2(oracle.getObjectName());
                procedure.setProcedureName2(oracle.getProcedureName());
            }
            resultList.add(procedure);
            indexNo++;
        }

        if (ObjectUtils.isEmpty(joinNames)){
            procedure = new OracleProcedureVO();
            procedure.setNo(String.valueOf(indexNo));
            procedure.setNo2(String.valueOf(indexNo));
            procedure.setProcedureName("数据源"+ dscVO.getDsFirst()+"没有"+type);
            procedure.setProcedureName2("数据源"+ dscVO.getDsSecond()+"没有"+type);
        }
        return resultList;
    }

}
