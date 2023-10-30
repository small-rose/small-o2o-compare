package com.small.o2o.comp.module.facade.base;

import com.small.o2o.comp.core.excel.MultipleSheelPropety;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/30 030 22:35
 * @version: v1.0
 */
public abstract class MetaDataCompare {

    /*
     * 抽象方法，校验数据源里的数据类型是否可以比较
     */
    protected boolean check(){
        return true;
    }

    /*
     * 抽象方法，组装数据源里的数据
     */
    protected abstract List<MultipleSheelPropety> queryData();

    /*
     * 抽象方法，组装数据源里的数据
     */
    protected abstract String generateExcel(String path, List<MultipleSheelPropety> result);


    /*
     * 抽象方法，组装数据源里的数据
     */
    //protected abstract void returnResult(String path, HttpResponse response);


    /*
     * 抽象方法，模板方法
     */
    public final String doCompareHandler(String path){
        if (check()) {
            List<MultipleSheelPropety> result = queryData();
            path = generateExcel(path, result);
            return path;
        }
        return null;
    }

}
