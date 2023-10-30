package com.small.o2o.comp.core.exception;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/30 030 22:55
 * @version: v1.0
 */
public class DataCheckException  extends RuntimeException {

    public DataCheckException(String message) {
        super(message);
    }

    public DataCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
