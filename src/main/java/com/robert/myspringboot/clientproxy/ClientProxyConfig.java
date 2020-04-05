package com.robert.myspringboot.clientproxy;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientProxyConfig {

    @Value("${users.api.url.v1}")
    private String usersEndpointUrl;

    @Bean
    public UserControllerV1 getUserControllerV1(){
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(usersEndpointUrl);
        UserControllerV1 proxy = target.proxy(UserControllerV1.class);
        return proxy;
    }
}
