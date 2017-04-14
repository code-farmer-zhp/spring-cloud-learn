package com.zhp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${depen.service.name}")
    private String serviceName;

    @RequestMapping("/client/test")
    public String getServiceTest() {
        return restTemplate.getForObject("http://" + serviceName + "/service/test", String.class);
    }
}
