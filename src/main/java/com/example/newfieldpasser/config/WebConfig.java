package com.example.newfieldpasser.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*===========================
        CORS
    ===========================*/

    // CORS origin & method 설정
    @Override
    public void addCorsMappings(CorsRegistry reg){
        // 모든 서비스 input에 대해 아래설정 적용
        reg.addMapping("/**")
                // 모든 Origin 허용
                .allowedOrigins("*")
                // 기재한 방식으로의 접근시 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                // 설정 유지기간 30분
                .maxAge(1800);
    }

}
