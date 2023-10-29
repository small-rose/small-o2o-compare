package com.small.o2o.comp.demo;

import com.small.o2o.comp.base.SmallO2oCompAppTest;
import com.small.o2o.comp.module.service.meta.QueryMetaService;
import com.small.o2o.comp.module.service.metadata.ObjectInfoService;
import com.small.o2o.comp.module.vo.DSQueryPramsVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/29 029 20:31
 * @version: v1.0
 */
public class TableInfoTests extends SmallO2oCompAppTest {

    @Autowired
    private ObjectInfoService objectInfoService;
    @Autowired
    private QueryMetaService queryMetaService;

    @Test
    public void info(){

        DSQueryPramsVO oracle_dev = DSQueryPramsVO.builder().dataSourceName("ORACLE_DEV").build();
        queryMetaService.getObjectInfo(oracle_dev);
    }
}
