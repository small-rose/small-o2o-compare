package com.small.o2o.comp.demo;

import com.small.o2o.comp.base.SmallO2oCompAppTest;
import com.small.o2o.comp.module.demo.DemoTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ DemoTestServiceTests ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/10/26 22:13
 * @Version ： 1.0
 **/
public class DemoTestServiceTests extends SmallO2oCompAppTest {

    @Autowired
    private DemoTestService demoTestService;

    @Test
    public void demo01( ) {
        demoTestService.selectTest("ORACLE_DEV", "MYSQL_57");
    }
}
