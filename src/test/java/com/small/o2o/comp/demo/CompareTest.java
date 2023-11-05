package com.small.o2o.comp.demo;

import com.small.o2o.comp.base.SmallO2oCompAppTest;
import com.small.o2o.comp.module.compare.CompareMetaDataService;
import com.small.o2o.comp.module.param.DsCompareParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
 * @Description : [ CompareTest ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/4 1:31
 * @Version ： 1.0
 **/
public class CompareTest extends SmallO2oCompAppTest {

    @Autowired
    private CompareMetaDataService compareMetaDataService;

    @Test
    public void all(){

        DsCompareParam vo = DsCompareParam.builder().dsFirst(CompareEnv.ORACLE_DEV.name())
                .dsSecond(CompareEnv.ORACLE_SIT.name()).build();
        compareMetaDataService.doCompareHandler(vo);
    }


    @Test
    public void oneTable(){

        DsCompareParam vo = DsCompareParam.builder().dsFirst("ORACLE_DEV").dsSecond("ORACLE_SIT").build();
        compareMetaDataService.doCompareHandler(vo);
    }

    enum CompareEnv{
        ORACLE_DEV,
        ORACLE_SIT,
        OB_ORACLE_DEV,
        OB_ORACLE_SIT;

    }
}
