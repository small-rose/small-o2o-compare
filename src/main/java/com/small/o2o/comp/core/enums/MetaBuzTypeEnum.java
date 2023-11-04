package com.small.o2o.comp.core.enums;

/**
 * @Project : small-o2o-compare
 * @Author : 张小菜
 * @Description : [ MetaBuzTypeEnum ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/4 0:32
 * @Version ： 1.0
 **/
public enum MetaBuzTypeEnum {

    META_OBJECT(0, "OBJECT","元数据信息汇总"),
    META_TABLE(1, "TABLE","表信息"),
    META_TAB_COLUMNS(2, "COLUMNS","表对应的列"),
    META_TAB_VIEW(3, "VIEW","表视图"),
    META_TAB_INDEX(4, "INDEX","表索引"),
    META_TAB_PRIMARY_KEY(5, "PRIMARY_KEY","表主键"),

    META_SEQUENCES(6, "SEQUENCES","序列"),
    META_TYPE(7, "TYPE","集合"),
    META_FUNCTION(8, "FUNCTION","函數"),
    META_PROCEDURE(9, "PROCEDURE","存储过程"),
    META_PACKAGE_LIST(10, "PACKAGE","包清单"),
    META_PACKAGE_PROC(11, "PACKAGE","存过包"),
    META_TAB_PARTITION(12, "META_TAB_PARTITION","分区表统计"),

    NULL(13, "TABLE_CHA","查表"),
    TABLE_CHA(14, "TABLE_CHA","查表"),
    TABLE_COLUMN_CHA(15, "TABLE_COLUMN_CHA","查列"),
    PACKAGE_NAME(16, "PACKAGE","包数量");


    private int index ;
    private String code ;
    private String desc ;

    MetaBuzTypeEnum(int index, String code, String desc) {
        this.index = index;
        this.code = code;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MetaBuzTypeEnum getSheetNameEnum(int index) {
        for(MetaBuzTypeEnum sheetNameEnum : MetaBuzTypeEnum.values()){
            if (index==sheetNameEnum.getIndex()){
                return sheetNameEnum ;
            }
        }
        return null;
    }
}