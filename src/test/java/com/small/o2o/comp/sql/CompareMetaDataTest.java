package com.small.o2o.comp.sql;


import com.small.o2o.comp.base.SmallO2oCompAppTest;
import org.junit.jupiter.api.Test;

public class CompareMetaDataTest extends SmallO2oCompAppTest {


    @Test
    public void compareTest(){
        //compareMetaDataService.doCompareHandler(path);
    }





    // 提出 Ob差表
    @Test
    public void chaBiaoOb(){
        //tableAndColumnService.chaBiao_ObMore();
    }

    // 提出 oracle 差表
    @Test
    public void chaBiaoOracle(){
        //tableAndColumnService.chaBiao_OracleMore();
    }


    // 提出ob差表SQL
    @Test
    public void chaBiaoSQL_ob(){
       // tableAndColumnService.chaBiaoSQL("ob");
    }
    // 提出oracle差表SQL
    @Test
    public void chaBiaoSQL_oracle(){
       // tableAndColumnService.chaBiaoSQL("oracle");
    }

    @Test
    public void test1(){

        boolean result = "CREATE TABLE \"AMSDB01\".\"AMS_COMMONMIRROR_BAK_QY\" (".toUpperCase().contains("AMS_COMMONMIRROR_BAK_QY".toUpperCase());
        System.out.println(result);
    }


    // 提出oracle差表SQL
    @Test
    public void chaBiaoSQL_90(){
        //tableAndColumnService.chaBiaoSQL("90");
    }

}
