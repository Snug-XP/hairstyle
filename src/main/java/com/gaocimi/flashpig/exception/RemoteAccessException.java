package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:57
 * @description 远程访问时错误异常
 */
public class RemoteAccessException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297075L;

    public RemoteAccessException() {
        super();
    }

    public RemoteAccessException(Object data) {
        super();

        super.data = data;
    }

    public RemoteAccessException(ResultCode resultCode) {
        super(resultCode);
    }

    public RemoteAccessException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public RemoteAccessException(String msg) {
        super(msg);
    }

    public RemoteAccessException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
