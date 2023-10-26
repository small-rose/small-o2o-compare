package com.small.o2o.comp.module.service.demo;

import com.small.o2o.comp.config.datasource.DynamicDataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
 * @Description : [ DemoTestService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/10/26 22:05
 * @Version ： 1.0
 **/

@Service
public class DemoTestService {

    @Autowired
    private JdbcTemplate jdbcTemplate ;

    public void selectTest(String ds1, String ds2){

        DynamicDataSourceContextHolder.setDataSourceType(ds1);
        String sql1 = "SELECT BANNER FROM V$VERSION where rownum =1 ";
        String result = jdbcTemplate.queryForObject(sql1,String.class);
        System.out.println("result1 = " + result);
        DynamicDataSourceContextHolder.removeDataSourceType();

        DynamicDataSourceContextHolder.setDataSourceType(ds2);
        String sql2 = "SELECT CONCAT('Mysql ', VERSION()) FROM DUAL ";
        String result2 = jdbcTemplate.queryForObject(sql2,String.class);
        System.out.println("result2 = " + result2);
        DynamicDataSourceContextHolder.removeDataSourceType();
    }
}
