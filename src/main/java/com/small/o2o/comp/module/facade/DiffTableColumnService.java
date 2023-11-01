package com.small.o2o.comp.module.facade;


import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
 public class DiffTableColumnService {

    @Autowired
    private QueryMetaDataService queryMetaService;
    @Autowired
    private CompareMetaDataService compareMetaDataService;
    /**
     * 找出第一个数据源有， 但是 第二个数据源 沒有的表
     */
    public List<String> diffTableFirstMore(){

        compareMetaDataService.check();

        DSCompareVO dscVO = MetaDataContextHolder.getDsCompare();

        List<String> obNames = new ArrayList<>();
        List<String> oraNames = new ArrayList<>();
        DSQueryPramsVO queryPramsVO = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsFirst()).tableName(dscVO.getTable()).build();


        List<ObTableInfoVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableInfoVO.class);

        DSQueryPramsVO queryPramsVO2 = DSQueryPramsVO.builder().dataSourceName(dscVO.getDsSecond()).tableName(dscVO.getTable()).build();

        List<ObTableInfoVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTableInfoVO.class);

        obObjList.stream().forEach(p->obNames.add(p.getTableName()));
        oraObjList.stream().forEach(p->oraNames.add(p.getTableName()));

        obNames.removeAll(oraNames);

        obNames.forEach(System.out::println);
        return obNames ;
    }

}
