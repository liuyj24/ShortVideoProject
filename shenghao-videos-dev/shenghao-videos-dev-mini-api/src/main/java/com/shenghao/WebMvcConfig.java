package com.shenghao;

import com.shenghao.controller.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置资源映射, /** : 访问到所有资源; file : 本地资源
        registry.addResourceHandler("/**")
                .addResourceLocations("file:C:/workspace_wxxcx/shenghao_videos_dev/")
                .addResourceLocations("classpath:/META-INF/resources/");//配置swagger2静态资源
    }

    @Bean(initMethod = "init")
    public ZKCuratorClient zKCuratorClient(){
        return new ZKCuratorClient();
    }

    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(miniInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/upload", "/video/uploadCover")
                .addPathPatterns("/video/userLike", "/video/userUnLike")
                .addPathPatterns("/video/saveComment")
                .excludePathPatterns("/user/queryPublisher");//排除

        super.addInterceptors(registry);
    }
}
