package com.zhp.fegin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "cloud-app-service",path = "/service")
public interface FeginRemoteService {

    @RequestMapping(value = "/test",produces = MediaType.TEXT_PLAIN_VALUE)
    String getTest();
}
