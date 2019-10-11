package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:56
 * @description 数据未找到
 */
public class DataNotFoundException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297083L;

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(Object data) {
        super();

        super.data = data;
    }

    public DataNotFoundException(ResultCode resultCode) {
        super(resultCode);
    }

    public DataNotFoundException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public DataNotFoundException(String msg) {
        super(msg);
    }

    public DataNotFoundException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
