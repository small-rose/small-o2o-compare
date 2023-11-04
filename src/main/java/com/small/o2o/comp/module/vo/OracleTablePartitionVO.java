package com.small.o2o.comp.module.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class OracleTablePartitionVO extends ObTablePartitionVO {

    @ExcelProperty(value = "ORA小计", index = 8)
    @ColumnWidth(value = 8)
    private String count2;

    @ExcelProperty(value = "序号", index = 9)
    @ColumnWidth(value = 8)
    private String no2;
    //SELECT  TC.TABLE_NAME,  TC.COLUMN_NAME, TC.DATA_TYPE,TC.DATA_LENGTH
    //FROM SYS.USER_TAB_COLUMNS TC  ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC;

    @ExcelProperty(value = "ORA表名称", index = 10)
    @ColumnWidth(value = 35)
    private String tableName2;

    @ExcelProperty(value = "ORA一级分区方式", index = 11)
    @ColumnWidth(value = 18)
    private String partType2;

    @ExcelProperty(value = "ORA一级分区字段", index = 12)
    @ColumnWidth(value = 18)
    private String partColumnName2;

    @ExcelProperty(value = "ORA二级分区方式", index = 13)
    @ColumnWidth(value = 15)
    private String subpartType2;

    @ExcelProperty(value = "ORA二级分区字段", index = 14)
    @ColumnWidth(value = 18)
    private String subpartColumnName2;

    @ExcelIgnore
    private Long partitionCount2;


}
