package com.small.o2o.comp.core.excel;


import com.alibaba.excel.metadata.Sheet;
import lombok.Data;

import java.util.List;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
@Data
public class MultipleSheelPropety {


    private List<? extends SheetDataVO> data;
    private List<? extends SheetDataVO> data2;

    private Sheet sheet;
}
