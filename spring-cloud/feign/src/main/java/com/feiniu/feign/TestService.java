package com.feiniu.feign;

import com.feiniu.feign.service.api.FeignController;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "feign-service", fallback = TestServiceFallback.class,
        configuration = FeignFullLogConfiguration.class
)
public interface TestService extends FeignController {

}
