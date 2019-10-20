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


    public String getFileHost() {
        return fileHost;
    }

    public void setFileHost(String fileHost) {
        this.fileHost = fileHost;
    }

    public String getEndpoint() {
		return endpoint;
	}

	public String getAccessId() {
		return accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getBucket() {
		return bucket;
	}

	public String getDir() {
		return dir;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAesKey() {
		return aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	public String getMsgDataFormat() {
		return msgDataFormat;
	}

	public void setMsgDataFormat(String msgDataFormat) {
		this.msgDataFormat = msgDataFormat;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getMchKey() {
		return mchKey;
	}

	public void setMchKey(String mchKey) {
		this.mchKey = mchKey;
	}

	public String getSubAppId() {
		return subAppId;
	}

	public void setSubAppId(String subAppId) {
		this.subAppId = subAppId;
	}

	public String getSubMchId() {
		return subMchId;
	}

	public void setSubMchId(String subMchId) {
		this.subMchId = subMchId;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getTemplateData() {
		return templateData;
	}

	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getEmphasisKeyword() {
		return emphasisKeyword;
	}

	public void setEmphasisKeyword(String emphasisKeyword) {
		this.emphasisKeyword = emphasisKeyword;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}




}
