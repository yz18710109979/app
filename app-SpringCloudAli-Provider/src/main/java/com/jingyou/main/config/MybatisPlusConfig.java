package com.jingyou.main.config;

import com.baomidou.mybatisplus.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.jingyou.main.core.interceptor.MybatisInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(order = 2)
@MapperScan("com.jingyou.main.**.repository")
public class MybatisPlusConfig {
    /**
     * mybatis-plus分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() { return new PaginationInterceptor();}
    /**
     * 乐观锁mybatis插件
     * @return
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * 自定义Mybatis插件, 为了拦截将要执行的SQL
     * @return
     */
    @Bean
    public MybatisInterceptor mybatisInterceptor(){ return new MybatisInterceptor(); }
}
