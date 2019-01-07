package com.voidm.springcloud.utils.exception;

/**
 * 全局异常
 *
 * @author voidm
 * @date 2019/1/7
 */
public class GlobalException extends Exception {

    private int code;
    private String errorMsg;

    public GlobalException(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}