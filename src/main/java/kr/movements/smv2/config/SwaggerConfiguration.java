package kr.movements.smv2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @file name : SwaggerConfiguration.java
 * @project : spring-boot-init
 * @date : Feb 20, 2023
 * @author : "ckr"
 * @history:
 * @program comment :
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
            .apis(RequestHandlerSelectors.basePackage("kr.movements.smv2"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaData())
            .securityContexts(Collections.singletonList(securityContext()))
            .securitySchemes(Collections.singletonList(apiKey()));
//            .securitySchemes(Arrays.asList(securitySchema()));//kakao 로그인으로 바로 시작
	}

	private ApiInfo metaData() {
		return new ApiInfoBuilder()
            .title("Soil-Master REST API")
            .description("dev")
            .version("0.0.1")
            .termsOfServiceUrl("Terms of service")
            .license("Apache License Version 2.0")
            .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
            .build();
	}

    @Bean
    public UiConfiguration uiConfiguration(){
        return UiConfigurationBuilder
            .builder()
            .operationsSorter(OperationsSorter.ALPHA)
            .build();
    }

	private ApiKey apiKey() {
		return new ApiKey("JWT", "Authorization", "header");
	}
//	private ApiKey apiKey() {
//		return new ApiKey("JWT", "Kakao-Authorization", "header");
//	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
	}

	//oauth2 설정 추가
	@Value("${oauth2.server.url}")
	private String oauth2ServerUrl;
	private OAuth securitySchema() {
		final List<AuthorizationScope> authorizationScopeList = new ArrayList<>(2);

		authorizationScopeList.add(new AuthorizationScope("read", "read all"));
		authorizationScopeList.add(new AuthorizationScope("write", "access all"));

		final List<GrantType> grantTypes = new ArrayList<>(1);
		// password 기반으로 설정된 것 사용
		// 토큰 end point (http://localhost:8080/oauth/token)
		grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(oauth2ServerUrl+"/oauth/token"));

		return new OAuth("oauth2", authorizationScopeList, grantTypes);
	}
}
