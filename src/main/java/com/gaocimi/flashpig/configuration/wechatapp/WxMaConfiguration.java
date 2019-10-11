package com.gaocimi.flashpig.configuration.wechatapp;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.aliyun.oss.OSSClient;
import com.gaocimi.flashpig.configuration.SystemConfigProperty;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */

@Configuration
public class WxMaConfiguration {


    @Autowired
    private SystemConfigProperty SystemConfigProperty;

    @Bean
    public WxMaConfig maConfig() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(this.SystemConfigProperty.getAppid());
        config.setSecret(this.SystemConfigProperty.getSecret());
        config.setToken(this.SystemConfigProperty.getToken());
        config.setAesKey(this.SystemConfigProperty.getAesKey());
        config.setMsgDataFormat(this.SystemConfigProperty.getMsgDataFormat());

        return config;
    }

    @Bean
    public WxMaService wxMaService(WxMaConfig maConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(maConfig);
        return service;
    }


    @Bean
    public WxMaMsgService wxMaMsgService(WxMaService wxMaService) {
        WxMaMsgService service = new WxMaMsgServiceImpl(wxMaService);
        return service;
    }
    @Bean
    public WxPayService wxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(this.SystemConfigProperty.getAppid()));
        payConfig.setMchId(StringUtils.trimToNull(this.SystemConfigProperty.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(this.SystemConfigProperty.getMchKey()));
        payConfig.setSubAppId(StringUtils.trimToNull(this.SystemConfigProperty.getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(this.SystemConfigProperty.getSubMchId()));
        payConfig.setKeyPath(StringUtils.trimToNull(this.SystemConfigProperty.getKeyPath()));

        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }


    @Bean
    public OSSClient oSSClient(){
        OSSClient client = new OSSClient(
                this.SystemConfigProperty.getEndpoint(),
                this.SystemConfigProperty.getAccessId(),
                this.SystemConfigProperty.getAccessKey());
        return client;
    }

    @Bean
    public WxMaTemplateMessage wxMaTemplateMessage(){
        WxMaTemplateMessage wxMaTemplateMessage=new WxMaTemplateMessage();
        wxMaTemplateMessage.setColor(this.SystemConfigProperty.getColor());
        wxMaTemplateMessage.setEmphasisKeyword(this.SystemConfigProperty.getEmphasisKeyword());
        wxMaTemplateMessage.setFormId(this.SystemConfigProperty.getFormId());
        wxMaTemplateMessage.setPage(this.SystemConfigProperty.getPage());
        wxMaTemplateMessage.setTemplateId(this.SystemConfigProperty.getTemplateId());
        wxMaTemplateMessage.setToUser(this.SystemConfigProperty.getTouser());
        return wxMaTemplateMessage;
    }



}
