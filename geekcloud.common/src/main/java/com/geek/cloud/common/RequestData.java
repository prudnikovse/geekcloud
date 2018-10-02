package com.geek.cloud.common;

import java.io.Serializable;

public class RequestData implements Serializable {
    private Action _action;
    private Object _data;

    public Action getAction() {
        return _action;
    }

    public void setAction(Action action) {
        _action = action;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }
}
