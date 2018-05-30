package com.yuzhi.fine.data;

import java.io.Serializable;

public class FounderHttpResponse<T> implements Serializable {

    private int code;
    private String message;
    private T info;

    public FounderHttpResponse() {
    }

    public FounderHttpResponse(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "FounderHttpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", info=" + info +
                '}';
    }
}
