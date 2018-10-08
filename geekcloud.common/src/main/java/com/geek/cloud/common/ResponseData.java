package com.geek.cloud.common;

import java.io.Serializable;

public class ResponseData implements Serializable {
    private boolean _success;
    private Action _action;
    private String _message;
    private Object _data;

    public boolean isSuccess() {
        return _success;
    }

    public void setSuccess(boolean success) {
        _success = success;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }

    public Action getAction() {
        return _action;
    }

    public void setAction(Action action) {
        _action = action;
    }
}
