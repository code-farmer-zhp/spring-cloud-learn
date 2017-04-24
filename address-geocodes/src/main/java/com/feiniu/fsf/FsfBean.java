package com.feiniu.fsf;

import com.feiniu.fsf.core.FsfApplication;
import com.feiniu.fsf.register.FsfRegister;
import com.feiniu.fsf.rpc.proxy.ClientProxyBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FsfBean {

    @Value("${register}")
    private String register;

    @Bean
    public FsfApplication fsfApplication() {
        FsfApplication fsfApplication = new FsfApplication();
        fsfApplication.setApplication("address-geocodes");
        return fsfApplication;
    }

    @Bean
    public FsfRegister fsfRegister() {
        FsfRegister fsfRegister = new FsfRegister();
        fsfRegister.setRegister(register);
        return fsfRegister;
    }

    @Bean
    public ClientProxyBean clientProxyBean() {
        ClientProxyBean clientProxyBean = new ClientProxyBean();
        clientProxyBean.setServiceInterface("com.feiniu.tms.interfaces.warehouse.IWarehouseDeliveryService");
        clientProxyBean.setServiceName("warehouseDeliveryService");
        return clientProxyBean;
    }
}
