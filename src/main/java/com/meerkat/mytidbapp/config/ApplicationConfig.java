package com.meerkat.mytidbapp.config;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * SpringBoot应用配置
 */
@Slf4j
@Configuration
public class ApplicationConfig {

    /**
     * 通过Spring内置Jackson构造器， 全局统一 Json序列化格式、反序列化格式配置
     *
     * @return Jackson2ObjectMapperBuilder
     */
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return Jackson2ObjectMapperBuilder
                .json()
                .indentOutput(true)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
//                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")))
                .deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        //配置HTTP超时时间 单位是毫秒
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(120000);
        httpRequestFactory.setReadTimeout(120000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        return restTemplate;
    }

}
