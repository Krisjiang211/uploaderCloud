package com.xiangyueEducation.uploaderCloud.config;

import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.Utils.PathEnum;
import com.xiangyueEducation.uploaderCloud.interceptor.AdminMotionInterceptor;
import com.xiangyueEducation.uploaderCloud.interceptor.ClientIpHandler;
import com.xiangyueEducation.uploaderCloud.interceptor.LoginInterceptor;
import com.xiangyueEducation.uploaderCloud.interceptor.RequestMappingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


@Configuration
public class MySpringMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private RequestMappingInterceptor requestMappingInterceptor;
    @Autowired
    private AdminMotionInterceptor adminMotionInterceptor;
    @Autowired
    private PathUtils pathUtils;

    private List<String> adminVerifyURL= Arrays.asList(
            "/admin/admin/register",//注册一个管理员
            "/admin/type/file/add","/admin/type/file/del","/admin/type/file/update",//对文件种类进行增删改
            "/admin/type/fileGroup/add","/admin/type/fileGroup/del","/admin/type/fileGroup/update",//对文件组进行增删改
            //以下是对 主副类的 增删 操作
            "admin/type/mainType/add","admin/type/viceType/add","admin/type/mainAndViceType/add",
            "admin/type/mainType/del","admin/type/viceType/del","admin/type/mainAndViceType/del",
            "/admin/user/get/all/**","/admin/user/add","/admin/user/disabled/**","/admin/user/edit/**"//对普普通用户进行增删改查
    );

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/headline/**");
        registry.addInterceptor(requestMappingInterceptor).addPathPatterns("/**");
//        registry.addInterceptor(adminMotionInterceptor).addPathPatterns(adminVerifyURL);
    }

    //跨域配置(可以使用,但先禁用)
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")//允许所有的接口被访问
//                .allowedOrigins("http://172.22.127.134")//允许所有来源
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .maxAge(3600); //1小时内不需要再预检（发送OPTIONS请求）
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //TODO 生产环境静态资源配置映射
        //头像寻找配置
        registry.addResourceHandler("/avatar/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"avatar/", PathEnum.PRODUCE_ENV.getPath()+"avatar/");
        //workSpace文件查找配置
        registry.addResourceHandler("/workSpace/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"workSpace/",PathEnum.PRODUCE_ENV.getPath()+"workSpace/");
        //selfSpace文件查找配置
        registry.addResourceHandler("/selfSpace/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"selfSpace/",PathEnum.PRODUCE_ENV.getPath()+"selfSpace/");
        //icon图标返回
        /**
         * 开发生产环境均可保留
         */
        registry.addResourceHandler("/fileType/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"fileType/",PathEnum.PRODUCE_ENV.getPath()+"fileType/");
        //文件组预览图片返回
        registry.addResourceHandler("/img/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"image/",PathEnum.PRODUCE_ENV.getPath()+"image/");


        // TODO 开发环境静态资源配置映射
//        //头像寻找配置
//        registry.addResourceHandler("/avatar/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"avatar/");
//        //workSpace文件查找配置
//        registry.addResourceHandler("/workSpace/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"workSpace/");
//        //selfSpace文件查找配置
//        registry.addResourceHandler("/selfSpace/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"selfSpace/");
//        //icon图标返回
//        /**
//         * 开发生产环境均可保留
//         */
//        registry.addResourceHandler("/fileType/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"fileType/");
//        //文件组预览图片返回
//        registry.addResourceHandler("/img/**").addResourceLocations(PathEnum.DEV_ENV.getPath()+"image/");


        //TODO 共用配置
        registry.addResourceHandler("/QRCode/**").addResourceLocations("file:"+PathEnum.QRCodeDirPath.getPath()+"/");

    }
}
