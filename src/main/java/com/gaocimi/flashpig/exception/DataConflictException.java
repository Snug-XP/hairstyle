package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:57
 * @description 数据冲突
 */
public class DataConflictException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297082L;

    public DataConflictException() {
        super();
    }

    public DataConflictException(Object data) {
        super();

        super.data = data;
    }

    public DataConflictException(ResultCode resultCode) {
        super(resultCode);
    }

    public DataConflictException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public DataConflictException(String msg) {
        super(msg);
    }

    public DataConflictException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
