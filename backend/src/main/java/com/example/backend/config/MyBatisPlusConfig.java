package com.example.backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * 分页插件等扩展功能可按需添加
 */
@Configuration
public class MyBatisPlusConfig {

    // 如需分页功能，取消以下注释即可
    // @Bean
    // public MybatisPlusInterceptor mybatisPlusInterceptor() {
    //     MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    //     interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    //     return interceptor;
    // }
}
