package com.devquest.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class R<T> implements Serializable {

    private int code;
    private String msg;
    private T data;
    private long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(String msg, T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String msg) {
        return fail(500, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        return r;
    }
}
