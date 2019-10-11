package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:54
 * @description 用户未登录异常
 */
public class UserNotLoginException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297075L;

    public UserNotLoginException() {
        super();
    }

    public UserNotLoginException(Object data) {
        super();

        super.data = data;
    }

    public UserNotLoginException(ResultCode resultCode) {
        super(resultCode);
    }

    public UserNotLoginException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

    public UserNotLoginException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
