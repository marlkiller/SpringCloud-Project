package com.voidm.springcloud.utils.exception;

/**
 * @author voidm
 * @date 2019/1/7
 */
public class ApiResult {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}