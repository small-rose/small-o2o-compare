package com.small.o2o.comp.module.service.oracle;

import com.small.o2o.comp.module.vo.DSQueryPramsVO;

import java.util.List;

/**
 * @Project : small-o2o-compare
 * @Author : zhangzongyuan
 * @Description : [ BuzTypeService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/1 21:53
 * @Version ： 1.0
 **/
public interface BuzTypeService {


    /**
     * 获取需要查询的类型
     * @return
     */
    public String getBuzType();

    <T> List<T> getCompareMetaList(DSQueryPramsVO queryPramsVO, Class clazz);
}
