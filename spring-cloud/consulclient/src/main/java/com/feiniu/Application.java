//package com.feiniu;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///*
//*@author: Max
//*@mail:1069905071@qq.com
//*@time:2017/8/1 0:47
//*/
//@Configuration
//@EnableAutoConfiguration
//@EnableDiscoveryClient
//@RestController
//@RefreshScope
//public class Application {
//
//    @RequestMapping("/")
//    public String home() {
//        return "Hello World";
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//
//    /*
//        *@author: Max
//        *@mail:1069905071@qq.com
//        *@time:2017/8/1 1:23
//        */
//    @RestController
//    public static class ServiceGetController {
//        @Value("${first:2}")
//        private String first;
//        @Autowired
//        private DiscoveryClient discoveryClient;
//
//        @RequestMapping("/getInstance/{serviceName}")
//        @ResponseBody
//        public String serviceUrl(@PathVariable("serviceName")String serviceName) {
//            serviceName= StringUtils.isEmpty(serviceName)?"":serviceName;
//            List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
//            if (list != null && list.size() > 0 ) {
//                return list.get(0).getUri().toString();
//            }
//            return null;
//        }
//
//        @Value("${name}")
//        private String name;
//
//        @RequestMapping("/getValue")
//        public String home() {
//            return name;
//        }
//
//        @RequestMapping("/getFirst")
//        @ResponseBody
//        public String getfirst() {
//            return first;
//        }
//    }
//}
