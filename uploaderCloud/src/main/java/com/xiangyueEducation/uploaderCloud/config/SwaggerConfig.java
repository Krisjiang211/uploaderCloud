package com.xiangyueEducation.uploaderCloud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springOpenAPI() {
        // 访问路径：http://localhost:9090/swagger-ui/index.html
        return new OpenAPI().info(new Info()
                .title("uploaderCloud文档调试")
                .description("真帅man")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Kris_Jiang")
                        .url("https://你的个人网站.com")
                        .email("xjunjiang5@gmail.com"))
        );
    }
}