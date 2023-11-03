package com.small.o2o.comp.core.constants;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
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


}
