package com.dexin.eccteacher.bean;

import org.jetbrains.annotations.Contract;

/**
 * 响应结果 实体
 */
public final class ResponseResultBean {
    /**
     * code : 0
     * message : 操作成功
     */

    private int code;
    private String message;

    @Contract(pure = true)
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Contract(pure = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
