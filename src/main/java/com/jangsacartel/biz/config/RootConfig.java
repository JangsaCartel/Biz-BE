package com.jangsacartel.biz.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
@MapperScan(basePackages = "com.jangsacartel.biz.**.mapper")
@ComponentScan(basePackages = {
    "com.jangsacartel.biz.auth.service",
    "com.jangsacartel.biz.global.jwt.filter",
    "com.jangsacartel.biz.global.jwt.util",
    "com.jangsacartel.biz.config",
    "com.jangsacartel.biz.scheduler"
})
public class RootConfig {

    @Value("${jdbc.driver}")   private String driver;
    @Value("${jdbc.url}")      private String url;
    @Value("${jdbc.username}") private String username;
    @Value("${jdbc.password}") private String password;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName(driver);
        cfg.setJdbcUrl(url);
        cfg.setUsername(username);
        cfg.setPassword(password);
        return new HikariDataSource(cfg);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean f = new SqlSessionFactoryBean();
        f.setDataSource(dataSource());
        f.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
        f.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml")
        );
        return f.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
