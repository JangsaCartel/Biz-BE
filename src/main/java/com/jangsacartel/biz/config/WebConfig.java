package com.jangsacartel.biz.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.jangsacartel.biz")
public class WebConfig implements WebMvcConfigurer {

    // 정적/Swagger 리소스 핸들러
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry reg) {
        // 정적 파일 (원하면 사용)
        reg.addResourceHandler("/static/**")
           .addResourceLocations("classpath:/static/");

        // Springfox 2.x Swagger UI 리소스
        reg.addResourceHandler("/swagger-ui.html")
           .addResourceLocations("classpath:/META-INF/resources/");
        reg.addResourceHandler("/webjars/**")
           .addResourceLocations("classpath:/META-INF/resources/webjars/");
        reg.addResourceHandler("/swagger-resources/**")
           .addResourceLocations("classpath:/META-INF/resources/");
        reg.addResourceHandler("/v2/api-docs")
           .addResourceLocations("classpath:/META-INF/resources/");
    }

    // (선택) 파일 업로드 사용 시
    // @Bean
    // public CommonsMultipartResolver multipartResolver() {
    //     CommonsMultipartResolver r = new CommonsMultipartResolver();
    //     r.setDefaultEncoding("UTF-8");
    //     r.setMaxUploadSize(10 * 1024 * 1024);
    //     return r;
    // }
}
