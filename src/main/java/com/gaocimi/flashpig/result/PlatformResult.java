package com.gaocimi.flashpig.result;


import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @desc 平台通用返回结果
 *
 * @author zhumaer
 * @since 10/9/2017 3:00 PM
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PlatformResult implements Result {

    private static final long serialVersionUID = 874200365941306385L;

    private Integer code;

    private String msg;

    private Object data;

    private Date timestamp;

    public static PlatformResult success() {
        PlatformResult result = new PlatformResult();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(new Date());
        return result;
    }

    public static PlatformResult success(Object data) {
        PlatformResult result = new PlatformResult();
        result.setResultCode(ResultCode.SUCCESS);
        if(data==null){ result.setData(new Date()); }
        else{ result.setData(data); }
        return result;
    }

    public static PlatformResult failure(ResultCode resultCode) {
        PlatformResult result = new PlatformResult();
        result.setResultCode(resultCode);
        result.setData(new Date());
        return result;
    }

    public static PlatformResult failure(ResultCode resultCode, Object data) {
        PlatformResult result = new PlatformResult();
        result.setResultCode(resultCode);
        if(data==null){ result.setData(new Date()); }
        else{ result.setData(data); }
        return result;
    }

    public static PlatformResult failure(String message) {
        PlatformResult result = new PlatformResult();
        result.setCode(ResultCode.PARAM_IS_INVALID.code());
        result.setMsg(message);
        return result;
    }


    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }

    @Override
    public String toString(){
        if(null == this.data){
            this.setData(new Object());
        }
        if(null==this.timestamp){
            this.timestamp=new Date();
        }
        return JSON.toJSONString(this);
    }
}