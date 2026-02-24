package com.noob.haspringboot.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String code;
    private String message;
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("200");
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(String code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}