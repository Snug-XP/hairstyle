package com.gaocimi.flashpig.configuration.oss;

import com.aliyun.oss.OSSClient;
import com.gaocimi.flashpig.configuration.SystemConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liyutg
 * @Date 2018/9/26 21:04
 * @description
 */
@Configuration
public class OssConfiguration {
    @Autowired
    SystemConfigProperty systemConfig;

    @Bean
    public OSSClient oSSClient(){
        OSSClient client = new OSSClient(
                this.systemConfig.getEndpoint(),
                this.systemConfig.getAccessId(),
                this.systemConfig.getAccessKey());
        return client;
    }
}
