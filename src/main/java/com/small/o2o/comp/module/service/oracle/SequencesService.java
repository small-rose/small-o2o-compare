package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.config.datasource.DynamicDSContextHolder;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.facade.FilePickService;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObSequencesVO;
import com.small.o2o.comp.module.vo.OracleSequencesVO;
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
public class SequencesService  implements BuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    private FilePickService filePickService ;

    @Override
    public String getBuzType() {
        return O2OConstants.MetaBuzTypeEnum.SEQUENCES.getCode();
    }

    @Override
    public  List getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz) {
        return getSequences();
    }


    public List<OracleSequencesVO> getSequences() {
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        List<String> allNames = new ArrayList<>();
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        List<ObSequencesVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObSequencesVO.class);
        DynamicDSContextHolder.removeDataSourceType();

        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().queryType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();
        List<ObSequencesVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObSequencesVO.class);
        DynamicDSContextHolder.removeDataSourceType();

        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjList.forEach(p -> allNames.add(p.getSequenceName()));
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            oraObjList.forEach(p -> {
                if (allNames.contains(p.getSequenceName())) {
                    allNames.add(p.getSequenceName());
                }
            });
        }

        List<OracleSequencesVO> resultList = new ArrayList<>();
        OracleSequencesVO sequences = null;
        int indexNo = 1;
        log.info("getSequences 取并集大小为：" + allNames.size());

        for (String n : allNames) {
            sequences = new OracleSequencesVO();
            sequences.setNo(String.valueOf(indexNo));
            sequences.setNo2(String.valueOf(indexNo));

            if (indexNo == 1) {
                sequences.setCount1(String.valueOf(obObjList.size()));
                sequences.setCount2(String.valueOf(oraObjList.size()));
            }

            List<ObSequencesVO> obList = obObjList.stream().parallel().filter(p -> p.getSequenceName().equals(n)).collect(Collectors.toList());

            List<ObSequencesVO> oraList = oraObjList.stream().parallel().filter(p -> p.getSequenceName().equals(n)).collect(Collectors.toList());
            if (obList.size() > 0) {
                ObSequencesVO ob = obList.get(0);
                sequences.setSequenceName(ob.getSequenceName());
                sequences.setLastNumber(ob.getLastNumber());
            }
            if (oraList.size() > 0) {
                ObSequencesVO oracle = oraList.get(0);
                sequences.setSequenceName2(oracle.getSequenceName());
                sequences.setLastNumber2(oracle.getLastNumber());
            }
            resultList.add(sequences);
            indexNo++;
        }

        if (ObjectUtils.isEmpty(resultList)){
            sequences = new OracleSequencesVO();
            sequences.setNo(String.valueOf(indexNo));
            sequences.setNo2(String.valueOf(indexNo));
            sequences.setSequenceName("数据源"+ dscVO.getDsFirst()+"没有"+getBuzType());
            sequences.setSequenceName2("数据源"+ dscVO.getDsSecond()+"没有"+getBuzType());
            resultList.add(sequences);
        }
        return resultList;
    }

}
