package com.gaocimi.flashpig.configuration.ssh;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author liyutg
 * @Date 2018/10/1 16:10
 * @description
 */
@Configuration
public class Redirect2HttpsConfig {
    @Bean
    public Connector connector(){
        // 捕获http请求8081端口，并将其重定向到8080端口
        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");//设置将分配给通过此连接器收到的请求的方案
        connector.setScheme("http");
        connector.setPort(8081);
        connector.setSecure(false);//如果connector.setSecure（true），则http使用http，而https使用https；否则，如果connector.setSecure（false），则http重定向至https;
        connector.setRedirectPort(8080);//监听到http的端口号后转向到的https的端口号
        return connector;
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        // 对http请求添加安全性约束，将其转换为https请求
        TomcatServletWebServerFactory tomcat =new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context context){
                SecurityConstraint constant = new SecurityConstraint();
                constant.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constant.addCollection(collection);
                context.addConstraint(constant);
            }
        };
        tomcat.addAdditionalTomcatConnectors(connector());
        return tomcat;
    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.addAllowedMethod("OPTIONS");
//        corsConfiguration.addAllowedMethod("HEAD");
//        corsConfiguration.addAllowedMethod("GET");
//        corsConfiguration.addAllowedMethod("PUT");
//        corsConfiguration.addAllowedMethod("POST");
//        corsConfiguration.addAllowedMethod("DELETE");
//        corsConfiguration.addAllowedMethod("PATCH");
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsFilter(source);
//    }

}
