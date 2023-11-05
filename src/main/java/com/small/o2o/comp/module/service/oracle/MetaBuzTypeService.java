package com.small.o2o.comp.module.service.oracle;

import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.param.DsQueryPrams;

import java.util.List;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ BuzTypeService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/1 21:53
 * @Version ： 1.0
 **/
public interface MetaBuzTypeService {


    /**
     * 获取需要查询的类型
     * @return
     */
    public MetaBuzTypeEnum getBuzType();

    /**
     *  获取比较的集合
     * @param queryPramsVO
     * @param <T>
     * @return
     */
    <T> List<T> getCompareMetaList(DsQueryPrams queryPramsVO);


}
