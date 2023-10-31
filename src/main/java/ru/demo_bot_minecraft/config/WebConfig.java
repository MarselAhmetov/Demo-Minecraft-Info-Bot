package ru.demo_bot_minecraft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Value("${minecraft.server.address}")
    private String address;
    @Bean
    public FilterRegistrationBean<IpFilter> ipFilter() {
        FilterRegistrationBean<IpFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IpFilter(address));
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
