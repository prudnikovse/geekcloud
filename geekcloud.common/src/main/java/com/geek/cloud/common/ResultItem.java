package com.geek.cloud.common;

public class ResultItem<T>{
    private boolean _success;
    private T _data;
    private String _message;

    public boolean isSuccess() {
        return _success;
    }

    public void setSuccess(boolean success) {
        _success = success;
    }

    public T getData() {
        return _data;
    }

    public void setData(T data) {
        _data = data;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }
}
