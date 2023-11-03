package com.small.o2o.comp.module.service.oracle;


import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.service.meta.MetaDataContextHolder;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.vo.ObTableViewVO;
import com.small.o2o.comp.module.vo.OracleTableViewVO;
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
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/1 029 1:37
 * @version: v1.0
 */
@Slf4j
@Service
public class MetaTableViewListService implements MetaBuzTypeService {

    @Autowired
    private QueryMetaDataService queryMetaService;

    @Override
    public MetaBuzTypeEnum getBuzType() {
        return  MetaBuzTypeEnum.META_TAB_VIEW;
    }

    @Override
    public  List getCompareMetaList(DsQueryPrams queryPramsVO) {
        return getTableViewList();
    }

    /**
     *  视图查询比较结果
     * @return
     */
    public List<OracleTableViewVO> getTableViewList() {
        DsCompareParam dscVO = MetaDataContextHolder.getDsCompare();

        DsQueryPrams queryPramsVO = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsFirst()).build();
        DsQueryPrams queryPramsVO2 = DsQueryPrams.builder().metaBuzType(getBuzType()).dataSourceName(dscVO.getDsSecond()).build();

        List<ObTableViewVO> obObjList = queryMetaService.queryObjectList(queryPramsVO, ObTableViewVO.class);
        List<ObTableViewVO> oraObjList = queryMetaService.queryObjectList(queryPramsVO2, ObTableViewVO.class);

        Map<String, ObTableViewVO> obObjMap = null;
        Map<String, ObTableViewVO> oracleObjMap = null;
        if (!ObjectUtils.isEmpty(obObjList)) {
            obObjMap = obObjList.stream().collect(
                    Collectors.toMap(ObTableViewVO::getViewName, e->e ,(oldVal, newVal) -> oldVal));
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            oracleObjMap = oraObjList.stream().collect(
                    Collectors.toMap(o -> o.getViewName(), Function.identity(),(oldVal, newVal) -> oldVal));
        }


        List<String> allTables = new ArrayList<>();
        if (!ObjectUtils.isEmpty(obObjList)) {
            for (ObTableViewVO obTableInfoVO : obObjList) {
                allTables.add(obTableInfoVO.getViewName());

            }
        }
        if (!ObjectUtils.isEmpty(oraObjList)) {
            for (ObTableViewVO obTableInfoVO : oraObjList) {
                if (!allTables.contains(obTableInfoVO.getViewName())) {
                    allTables.add(obTableInfoVO.getViewName());
                }
            }
        }


        List<OracleTableViewVO> resultList = new ArrayList<>();
        OracleTableViewVO object = null;
        int indexNo = 1;
        log.info(" 查询视图取并集大小为：" + allTables.size());
        for (String n : allTables) {
            object = new OracleTableViewVO();
            object.setNo(String.valueOf(indexNo));
            object.setNo2(String.valueOf(indexNo));
            ObTableViewVO obTiv = obObjMap.get(n);
            ObTableViewVO oracleTiv = oracleObjMap.get(n);

            if (obTiv != null) {
                ObTableViewVO ob = obTiv;
                object.setViewName(ob.getViewName());
                object.setTextLength(ob.getTextLength());
                object.setText(ob.getText());
            }
            if (oracleTiv != null ) {
                ObTableViewVO oracle = oracleTiv;
                object.setViewName2(oracle.getViewName());
                object.setTextLength2(oracle.getTextLength());
                object.setText2(oracle.getText());
            }
            resultList.add(object);
            indexNo++;
        }
        return resultList;
    }
}
