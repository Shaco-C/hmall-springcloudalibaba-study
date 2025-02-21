package com.hmall.common.config;

import com.hmall.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ConditionalOnClass(DispatcherServlet.class)//让这个只在微服务中生效，在网关中不生效。gateway不包含springMVC
public class MvcConfig implements WebMvcConfigurer {


    //配置用户信息拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
