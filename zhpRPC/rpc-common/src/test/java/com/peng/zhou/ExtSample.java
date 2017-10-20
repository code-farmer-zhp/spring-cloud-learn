package com.peng.zhou;

import java.net.URL;

@SPI("extSample2")
public interface ExtSample {
    @Adaptive
    String sayHi(URL arg0);
}
