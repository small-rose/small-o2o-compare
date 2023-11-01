package com.small.o2o.comp.config.common;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/10/31 029 19:37
 * @version: v1.0
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Autowired
    Environment environment;

    @Bean
    public Docket createRestApi() throws UnknownHostException {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.small.o2o"))
                //.apis(RequestHandlerSelectors.withClassAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }
    public ApiInfo apiInfo() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String contextPath = environment.getProperty("server.servlet.context-path");
        String port = environment.getProperty("server.port");
        if (contextPath.endsWith("/")){
            contextPath = "";
        }
        return new ApiInfoBuilder()
                .title("small-o2o-compare api文档")
                .description("small-o2o-compare api文档")
                .termsOfServiceUrl("http://"+ip+":"+port+""+contextPath+"/swagger-ui.html, http://"+ip+":"+port+""+contextPath+"/doc.html")
                .version("version 1.0")
                .build();
    }
}
