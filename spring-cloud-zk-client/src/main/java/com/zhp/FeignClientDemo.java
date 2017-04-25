package com.zhp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${zk.service.name}")
public interface FeignClientDemo {

    @RequestMapping("/test")
    String test();

    @RequestMapping(value = "/postTest", method = RequestMethod.POST)
    String postTest(@RequestParam("data") String data);
}
