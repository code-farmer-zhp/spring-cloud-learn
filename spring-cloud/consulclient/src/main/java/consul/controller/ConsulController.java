package consul.controller;

import consul.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liaowen on 2017/7/24.
 */
@RestController
public class ConsulController {

    @Autowired
    private UserInfo userInfo;

    @RequestMapping(value = "/consulInfo")
    public String getConsulInfo(){
        return userInfo.toString();
    }



    @Value("${Daniel.address}")
    private String address;

    @RequestMapping(value = "/consulInfo2")
    public String getConsulInfo2(){
        return address;
    }



    private   static String email  ;

    @Value("${Daniel.email}")
    public  void setEmail(String email) {
        ConsulController.email = email;
    }

    @RequestMapping(value = "/consulInfo3")
    public String getConsulInfo3(){
        return email;
    }

}
