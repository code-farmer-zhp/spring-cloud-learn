package com.feiniu.webapi;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
@ConfigurationProperties(WebApiAutoConfiguration.PREFIX)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebApiAutoConfiguration {

    public static final String PREFIX = "springfox.api";

    private String group = "com.feiniu";

    private String title = "api 文档";

    private String description = "";

    private String version = "0.1";

    private String termsOfServiceUrl = "set termsOf-ServiceUrl via 'springfox.api.termsOfServiceUrl'";

    private String name = "set contact name via 'springfox.api.name'";

    private String email = "set contact email via 'springfox.api.email'";

    private String license;

    private String licenseUrl = "http://www.feiniu.com";

    private Map<Integer, String> status = new HashMap<>();

    @Bean
    public Docket api() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(group)
                .apiInfo(new ApiInfo(title, description, version, termsOfServiceUrl, new Contact(name, "", email), license, licenseUrl, new ArrayList<VendorExtension>()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(Date.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false);
        if (status.size() == 0) {
            ResponseMessage error = new ResponseMessageBuilder().code(500).message("服务出错啦~").build();
            docket.globalResponseMessage(RequestMethod.GET, Arrays.asList(error))
                    .globalResponseMessage(RequestMethod.POST, Arrays.asList(error));

        } else {
            List<ResponseMessage> lists = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : status.entrySet()) {
                Integer code = entry.getKey();
                String message = entry.getValue();
                ResponseMessage responseMessage = new ResponseMessageBuilder().code(code).message(message).build();
                lists.add(responseMessage);
            }
            docket.globalResponseMessage(RequestMethod.GET, lists)
                    .globalResponseMessage(RequestMethod.POST, lists)
                    .globalResponseMessage(RequestMethod.PUT, lists)
                    .globalResponseMessage(RequestMethod.DELETE, lists);

        }
        docket.forCodeGeneration(true);
        return docket;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public Map<Integer, String> getStatus() {
        return status;
    }

    public void setStatus(Map<Integer, String> status) {
        this.status = status;
    }
}
