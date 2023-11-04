package com.small.o2o.comp.module.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.small.o2o.comp.core.excel.SheetDataVO;
import lombok.Data;

@Data
public class ObTablePartitionVO extends SheetDataVO {

    @ExcelProperty(value = "OB小计", index = 0)
    @ColumnWidth(value = 8)
    private String count1;

    @ExcelProperty(value = "序号", index = 1)
    @ColumnWidth(value = 8)
    private String no;
    //SELECT  TC.TABLE_NAME,  TC.COLUMN_NAME, TC.DATA_TYPE,TC.DATA_LENGTH
    //FROM SYS.USER_TAB_COLUMNS TC  ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC;

    @ExcelProperty(value = "OB表名称", index = 2)
    @ColumnWidth(value = 35)
    private String tableName;

    @ExcelProperty(value = "OB一级分区方式", index = 3)
    @ColumnWidth(value = 18)
    private String partType;

    @ExcelProperty(value = "OB一级分区字段", index = 4)
    @ColumnWidth(value = 18)
    private String partColumnName;

    @ExcelProperty(value = "OB二级分区方式", index = 5)
    @ColumnWidth(value = 15)
    private String subpartType;

    @ExcelProperty(value = "OB二级分区字段", index = 6)
    @ColumnWidth(value = 18)
    private String subpartColumnName;

    @ExcelProperty(value = "OB分区数量", index = 7)
    @ColumnWidth(value = 10)
    private Long partitionCount;

    @ExcelIgnore
    private Long count ;

}
