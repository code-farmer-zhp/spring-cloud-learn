package consul;

import consul.model.UserInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by liaowen on 2017/5/27.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableConfigurationProperties({UserInfo.class})
//@ImportResource("classpath*:/*.xml")
public class ConsulApplication {

   public static void main(String[] args) {
       SpringApplication.run(ConsulApplication.class,args);
   }

}

