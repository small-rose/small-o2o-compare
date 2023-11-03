package com.small.o2o.comp.module.service.meta;

import com.small.o2o.comp.module.param.DsCompareParam;
import com.small.o2o.comp.module.vo.ObTableInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2022/3/12 15:20
 * @version: v1.0
 */
public class MetaDataContextHolder {

    public static final Logger log = LoggerFactory.getLogger(MetaDataContextHolder.class);

    /**
     *此类提供线程局部变量。这些变量不同于它们的正常对应关系是每个线程访问一个线程(通过get、set方法),有自己的独立初始化变量的副本。
     */
    private static final ThreadLocal<List<ObTableInfoVO>> tableHolder = new ThreadLocal<>();

    private static final ThreadLocal<DsCompareParam> dsCompareHolder = new ThreadLocal<>();

    /**
     * 设置当前线程的数据源变量
     */
    public static void setAllTableList(List<ObTableInfoVO> tableList) {
        log.info("已缓存到{}数据", tableList);
        tableHolder.set(tableList);
    }

    /**
     * 获取当前线程的数据源变量
     */
    public static List<ObTableInfoVO> getAllTableList() {
        return  tableHolder.get();
    }

    /**
     * 删除与当前线程绑定的数据源变量
     */
    public static void removeTableData() {
        tableHolder.remove();
    }


    /**
     * 设置当前线程的数据源变量
     */
    public static void setDsCompare(DsCompareParam dsCompare) {
        log.info("已缓存到dsCompare{}数据", dsCompare);
        dsCompareHolder.set(dsCompare);
    }

    /**
     * 获取当前线程的数据源变量
     */
    public static DsCompareParam getDsCompare() {
        return  dsCompareHolder.get();
    }

    /**
     * 删除与当前线程绑定的数据源变量
     */
    public static void removeDsCompare() {
        dsCompareHolder.remove();
    }

}
