package com.small.o2o.comp.demo;

import com.small.o2o.comp.base.SmallO2oCompAppTest;
import com.small.o2o.comp.core.enums.MetaBuzTypeEnum;
import com.small.o2o.comp.module.param.DsQueryPrams;
import com.small.o2o.comp.module.service.meta.QueryMetaDataService;
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
public class MetaInfoTests extends SmallO2oCompAppTest {


    @Autowired
    private QueryMetaDataService queryMetaService;

    @Test
    public void queryObjectInfo(){
        DsQueryPrams oracle_dev = DsQueryPrams.builder().metaBuzType(MetaBuzTypeEnum.META_OBJECT).dataSourceName("ORACLE_DEV").build();
        List<Object> objects = queryMetaService.queryObjectList(oracle_dev, ObObjectInfoVO.class);
        System.out.println("objects :" + objects);
    }

    @Test
    public void queryTableInfo(){
        DsQueryPrams oracle_dev = DsQueryPrams.builder().metaBuzType(MetaBuzTypeEnum.META_TABLE).dataSourceName("ORACLE_DEV").build();
        List<Object> objects = queryMetaService.queryObjectList(oracle_dev, ObObjectInfoVO.class);
        System.out.println("objects :" + objects);
    }

    @Test
    public void queryTableColumns(){
        DsQueryPrams oracle_dev = DsQueryPrams.builder().metaBuzType(MetaBuzTypeEnum.META_TAB_COLUMNS).dataSourceName("ORACLE_DEV").build();
        List<Object> objects = queryMetaService.queryObjectList(oracle_dev, ObObjectInfoVO.class);
        System.out.println("objects :" + objects);
    }

    @Test
    public void queryTableIndex(){
        DsQueryPrams oracle_dev = DsQueryPrams.builder().metaBuzType(MetaBuzTypeEnum.META_TAB_INDEX).dataSourceName("ORACLE_DEV").build();
        List<Object> objects = queryMetaService.queryObjectList(oracle_dev, ObObjectInfoVO.class);
        System.out.println("objects :" + objects);
    }
}
