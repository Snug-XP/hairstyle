package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:58
 * @description 系统内部错误异常
 */

public class InternalServerException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297074L;

    public InternalServerException() {
        super();
    }

    public InternalServerException(Object data) {
        super();

        super.data = data;
    }

    public InternalServerException(ResultCode resultCode) {
        super(resultCode);
    }

    public InternalServerException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public InternalServerException(String msg) {
        super(msg);
    }

    public InternalServerException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
