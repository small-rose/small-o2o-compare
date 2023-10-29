package com.small.o2o.comp.core.exception;

/**
 * @author zhangxiaocai
 */
public class BussinessException extends RuntimeException {

    public BussinessException(String message) {
        super(message);
    }

    public BussinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
