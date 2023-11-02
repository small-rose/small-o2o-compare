package com.small.o2o.comp.demo;

import com.small.o2o.comp.base.SmallO2oCompAppTest;
import com.small.o2o.comp.core.constants.O2OConstants;
import com.small.o2o.comp.module.compare.CompareMetaDataService;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
import com.small.o2o.comp.module.vo.DSCompareVO;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import com.small.o2o.comp.module.vo.ObObjectInfoVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 20:31
 * @version: v1.0
 */
public class TableInfoTests extends SmallO2oCompAppTest {

    @Autowired
    private CompareMetaDataService compareMetaDataService;
    @Autowired
    private QueryMetaDataService queryMetaService;
    @Test
    public void info(){

        DSQueryPramsVO oracle_dev = DSQueryPramsVO.builder().queryType(O2OConstants.SQL_OBJECT).dataSourceName("ORACLE_DEV").build();
          List<Object> objects = queryMetaService.queryObjectList(oracle_dev, ObObjectInfoVO.class);
        System.out.println("objects :" + objects);
    }


    @Test
    public void all(){

        DSQueryPramsVO oracle_dev = DSQueryPramsVO.builder().dataSourceName("ORACLE_DEV").build();
        String path = "D:\\game";
        DSCompareVO vo = DSCompareVO.builder().dsFirst("ORACLE_DEV").dsSecond("ORACLE_SIT").build();
        compareMetaDataService.doCompareHandler(vo);
    }
}
