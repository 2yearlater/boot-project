package com.artcher.config;

import com.artcher.common.JacksonObjectMapper;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * web容器中的配置类,配置所有用到的组件
 */

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 静态资源配置路径，否则不放在static和teplate中无法访问
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        log.info("开始静态资源配置");

        //配置静态资源所在目录
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");


    }

    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //创建分页插件
        PaginationInnerInterceptor page = new PaginationInnerInterceptor();
        page.setDbType(DbType.MYSQL);
        page.setOverflow(true);
        mybatisPlusInterceptor.addInnerInterceptor(page);

        return mybatisPlusInterceptor;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters){
        log.info("扩展消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器,底层使用jackson将java转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
