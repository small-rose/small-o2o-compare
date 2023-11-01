package com.small.o2o.comp.core.constants;

/**
 * @author zhangxiaocai
 */
public class O2OConstants {


    public static final String SQL_OBJECT = "OBJECT";
    public static final String SQL_TABLE = "TABLE";
    public static final String SQL_VIEW = "VIEW";
    public static final String SQL_TABLE_COLUMN = "TABLE_COLUMN";
    public static final String SQL_TABLE_PRIMARYKEY = "PRIMARYKEY";
    public static final String SQL_TABLE_INDEX = "INDEX";
    public static final String SQL_SEQUENCES = "SEQUENCES";
    public static final String SQL_TYPE = "TYPE";
    public static final String SQL_FUNCTION = "FUNCTION";
    public static final String SQL_PROCEDURE = "PROCEDURE";
    public static final String SQL_PACKAGE = "PACKAGE";


    public enum DsInitType{
        CONF("conf"),
        DATABASE( "db");

        private String value ;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        DsInitType(String value) {
            this.value = value;
        }
    }

    public enum DBType{
        ORACLE("ORACLE"),
        OB_ORACLE( "OB_ORACLE"),
        MYSQL("MYSQL"),
        OB_MYSQL( "OB_MYSQL");


        private String value ;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        DBType(String value) {
            this.value = value;
        }
    }

    public enum MetaBuzTypeEnum {
        OBJECT_INFO(0, "OBJECT","元数据信息汇总"),
        TABLE_INFO(1, "TABLE","表信息"),
        TableColumnVO(2, "TABLE_COLUMN","表对应的列"),
        TABLE_VIEW(3, "VIEW","表视图"),
        TABLE_INDEX(4, "INDEX","表索引"),


        SEQUENCES(5, "SEQUENCES","序列"),
        TYPE(6, "TYPE","集合"),
        FUNCTION(7, "FUNCTION","函數"),
        PROCEDEURE(8, "PROCEDURE","存储过程"),
        PACKAGE(9, "PACKAGE","存过包"),
        TABLE_CHA(10, "TABLE_CHA","查表"),
        TABLE_COLUMN_CHA(11, "TABLE_COLUMN_CHA","查列"),
        TABLE_PRIMARYKEY(12, "PRIMARYKEY","表主键"),
        PACKAGE_NAME(13, "PACKAGE","包数量");


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
}
