package com.jangsacartel.biz.config;

import javax.servlet.Filter; // (톰캣9면 javax)
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // 전역(비웹) 설정들
        return new Class<?>[]{
            RootConfig.class,
            SecurityConfig.class,
            RedisConfig.class,
            JacksonConfig.class,
            CorsConfig.class,     // ← CorsFilter 방식 사용할 때만 포함
            SwaggerConfig.class   // Docket은 웹 빈이라 WebConfig/Root 어느쪽이든 가능
            // SecurityConfig.class, RedisConfig.class, S3Config.class ... 나중에 추가
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // MVC 설정
        return new Class<?>[]{ WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        // 하나면 충분 (swagger 경로도 여기서 따로 나열할 필요 없음)
        return new String[]{ "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter enc = new CharacterEncodingFilter();
        enc.setEncoding("UTF-8");
        enc.setForceEncoding(true);
        return new Filter[]{ enc };
    }
}
