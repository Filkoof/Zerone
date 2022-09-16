package ru.example.group.main.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfig {
        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiInfo());
        }

    public ApiInfo apiInfo() {
        return new ApiInfo(
                "Zerone API",
                """
                        API for Zerone social network project.
                        
                        Please, for secured APIs!
                        RECEIVE TOKEN FIRST USING correct login and password with login API and put it into header name Authorization to get access to secured APIs.
                                                
                        """,
                "1.0",
                "http://195.161.62.32/terms_of_service",
                new Contact("API owner", "http://195.161.62.32/owner ", "owner@rmailer.org"),
                "Zerone_api_license",
                "http://195.161.62.32/licence",
                new ArrayList<>()
        );
    }

}
