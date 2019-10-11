package com.gaocimi.flashpig.exception;

import com.gaocimi.flashpig.result.ResultCode;

/**
 * @author liyutg
 * @date 2018/9/2 23:57
 * @description 无访问权限异常
 */
public class PermissionForbiddenException extends BusinessException {
    private static final long serialVersionUID = 3721036867889297075L;

    public PermissionForbiddenException() {
        super();
    }

    public PermissionForbiddenException(Object data) {
        super();

        super.data = data;
    }

    public PermissionForbiddenException(ResultCode resultCode) {
        super(resultCode);
    }

    public PermissionForbiddenException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public PermissionForbiddenException(String msg) {
        super(msg);
    }

    public PermissionForbiddenException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
