package com.gaocimi.flashpig.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liyutg
 * @Date 2018/9/26 21:06
 * @description
 */

@Component
@Data
public class SystemConfigProperty {
    /**
     * OSS存储设置，fileHost="https://" + this.getBucket() + "." + this.getEndpoint();
     */
    @Value("${oss.fileHost}")
    private String fileHost;

    /**
     * OSS存储设置，endpoint
     */
    @Value("${oss.endpoint}")
    private String endpoint;

    /**
     * OSS存储设置，accessId
     */
    @Value("${oss.accessId}")
    private String accessId;

    /**
     * OSS存储设置，accessKey
     */
    @Value("${oss.accessKey}")
    private String accessKey;

    /**
     * OSS存储设置，bucket
     */
    @Value("${oss.bucket}")
    private String bucket;

    /**
     * OSS存储设置，dir
     */
    @Value("${oss.dir}")
    private String dir;

    /**
     * 设置微信小程序的appid
     */
    @Value("${wechat.miniapp.appid}")
    private String appid;

    /**
     * 设置微信小程序的Secret
     */
    @Value("${wechat.miniapp.secret}")
    private String secret;

    /**
     * 设置微信小程序的token
     */
    @Value("${wechat.miniapp.token}")
    private String token;

    /**
     * 设置微信小程序的EncodingAESKey
     */
    @Value("${wechat.miniapp.aesKey}")
    private String aesKey;

    /**
     * 消息格式，XML或者JSON
     */
    @Value("${wechat.miniapp.msgDataFormat}")
    private String msgDataFormat;

    /**
     * 微信支付商户号
     */
    @Value("${wechat.miniapp.mchId}")
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    @Value("${wechat.miniapp.mchKey}")
    private String mchKey;

    /**
     * 服务商模式下的子商户公众账号ID
     */
    @Value("${wechat.miniapp.subAppId}")
    private String subAppId;

    /**
     * 服务商模式下的子商户号
     */
    @Value("${wechat.miniapp.subMchId}")
    private String subMchId;

    /**
     * p12证书的位置，可以指定绝对路径，也可以指定类路径（以classpath:开头）
     */
    @Value("${wechat.miniapp.keyPath}")
    private String keyPath;


    /**
     * 微信消息模板设置，touser
     */
    @Value("${wechat.message.templete.touser}")
    private String touser;


    /**
     * 微信消息模板设置，templateId
     */
    @Value("${wechat.message.templete.templateId}")
    private String templateId;


    /**
     * 微信消息模板设置，page
     */
    @Value("${wechat.message.templete.page}")
    private String page;

    /**
     * 微信消息模板设置，formId
     */
    @Value("${wechat.message.templete.formId}")
    private String formId;

    /**
     * 微信消息模板设置，data
     */
    @Value("${wechat.message.templete.data}")
    private String templateData;

    /**
     * 微信消息模板设置，color
     */
    @Value("${wechat.message.templete.color}")
    private String color;

    /**
     * 微信消息模板设置，emphasisKeyword
     */
    @Value("${wechat.message.templete.emphasisKeyword}")
    private String emphasisKeyword;

}
