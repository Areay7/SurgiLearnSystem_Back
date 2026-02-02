package com.discussio.resourc.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有域名跨域（不启用 credentials 时可用 *，避免跨域下载等场景出问题）
        config.addAllowedOriginPattern("*");
        // 允许所有请求头（包括 Authorization）
        config.addAllowedHeader("*");
        // 允许所有请求方法（含 GET/POST/OPTIONS 等）
        config.addAllowedMethod("*");
        // 不强制凭证模式，避免与 * 冲突导致跨域下载失败
        config.setAllowCredentials(false);
        // 暴露 Content-Disposition，供前端读取下载文件名
        config.addExposedHeader("Content-Disposition");
        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
