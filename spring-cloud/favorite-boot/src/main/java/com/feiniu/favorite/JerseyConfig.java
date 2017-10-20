package com.feiniu.favorite;


import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("com.feiniu.favorite");
    }
}
