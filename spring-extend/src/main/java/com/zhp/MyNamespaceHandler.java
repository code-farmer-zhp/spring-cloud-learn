package com.zhp;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MyNamespaceHandler extends NamespaceHandlerSupport {  
    public void init() {
        System.out.println("==============");
        registerBeanDefinitionParser("people", new PeopleBeanDefinitionParser());
    }  
}  