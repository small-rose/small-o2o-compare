package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.compare.FilePickService;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
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
public class MetaSequenceListService implements MetaBuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    private FilePickService filePickService ;

    @Override
    public MetaBuzTypeEnum getBuzType() {

        return MetaBuzTypeEnum.META_SEQUENCES;
    }

    @Override
    public  List getCompareMetaList(DsQueryPrams queryPramsVO) {
        return getSequences();
    }


    public List<OracleSequencesVO> getSequences() {
        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();

        List<String> allNames = new ArrayList<>();
        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        List<ObSequencesVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObSequencesVO.class);

        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();
        List<ObSequencesVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObSequencesVO.class);

        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjList.forEach(p -> allNames.add(p.getSequenceName()));
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            oraObjList.forEach(p -> {
                if (!allNames.contains(p.getSequenceName())) {
                    allNames.add(p.getSequenceName());
                }
            });
        }

        List<OracleSequencesVO> resultList = new ArrayList<>();
        OracleSequencesVO sequences = null;
        int indexNo = 1;
        log.info("查询 {} 取并集大小为：{}", getBuzType() , allNames.size());

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
