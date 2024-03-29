package com.gaocimi.flashpig.interceptor;

import com.gaocimi.flashpig.exception.handler.DefaultErrorResult;
import com.gaocimi.flashpig.result.PlatformResult;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.result.Result;
import com.gaocimi.flashpig.utils.RequestContextHolderUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Date;


/**
 * @desc 接口响应体处理器
 *
 * @author zhumaer
 * @since 4/1/2018 3:00 PM
 */
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ResponseResult responseResultAnn = (ResponseResult) RequestContextHolderUtil.getRequest().getAttribute
                (ResponseResultInterceptor.RESPONSE_RESULT);
        return responseResultAnn != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        ResponseResult responseResultAnn = (ResponseResult) RequestContextHolderUtil.getRequest().getAttribute(
                ResponseResultInterceptor.RESPONSE_RESULT);

        Class<? extends Result> resultClazz = responseResultAnn.value();

        if (resultClazz.isAssignableFrom(PlatformResult.class)) {
            if (body instanceof DefaultErrorResult) {
                DefaultErrorResult defaultErrorResult = (DefaultErrorResult) body;
                return PlatformResult.builder()
                        .code(defaultErrorResult.getCode())
                        .msg(defaultErrorResult.getMessage())
                        .data(defaultErrorResult.getErrors())
                        .time(new Date())
                        .build();
            }else if(body instanceof PlatformResult){
                PlatformResult platformResult = (PlatformResult) body;
                return PlatformResult.builder()
                        .code(platformResult.getCode())
                        .msg(platformResult.getMsg())
                        .data(platformResult.getData())
                        .time(new Date())
                        .build();
            }else {
                return PlatformResult.success(body);
            }
        }
        return body;
    }

}