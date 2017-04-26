package com.zhp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RestController
public class TestController {

    @Autowired
    private RestTemplate template;

    @Value("${zk.service.name}")
    private String service;

    @Value("${msg:defaultMsg}")
    private String msg;

    @Autowired
    private FeignClientDemo feignClientDemo;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/test")
    public String test() {
        return template.getForObject("http://" + service + "/test", String.class);
    }

    @RequestMapping("/test2")
    public String test2() {
        return feignClientDemo.test();
    }

    @RequestMapping("/postTest")
    public String postTest(String data) {
        return feignClientDemo.postTest(data);
    }

    @RequestMapping("/getServiceUrl")
    public List<ServiceInstance> getServiceUrl() {
        return discoveryClient.getInstances(service);
    }

    @RequestMapping("/getzkprop")
    public String getZkProp() {
        return msg;
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
