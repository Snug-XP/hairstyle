package com.gaocimi.flashpig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan //设置启动时spring能够扫描到我们自己编写的servlet和filter, 用于Druid监控
public class ServiceWeiXinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceWeiXinApplication.class, args);
    }
}
