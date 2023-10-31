package com.small.o2o.comp.module.service.metadata;


import com.small.o2o.comp.config.datasource.DynamicDSContextHolder;
import com.small.o2o.comp.module.facade.FilePickService;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObSequencesVO;
import com.small.o2o.comp.module.vo.OracleSequencesVO;
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
public class SequencesService {

    @Autowired
    private QueryMetaService queryMetaService;

    private FilePickService filePickService ;



    public List<OracleSequencesVO> getSequences() {
        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        List<String> allNames = new ArrayList<>();
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).build();
        List<ObSequencesVO> obObjList = queryMetaService.querySequencesVO(queryPramsVO);
        DynamicDSContextHolder.removeDataSourceType();

        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).build();
        List<ObSequencesVO> oraObjList = queryMetaService.querySequencesVO(queryPramsVO2);
        DynamicDSContextHolder.removeDataSourceType();

        obObjList.forEach(p -> allNames.add(p.getSequenceName()));
        oraObjList.forEach(p -> {
            if (allNames.contains(p.getSequenceName())) {
                allNames.add(p.getSequenceName());

            }
        });

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
        ;
        return resultList;
    }

}
