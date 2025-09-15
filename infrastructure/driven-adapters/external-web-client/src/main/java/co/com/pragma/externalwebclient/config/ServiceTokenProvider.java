package co.com.pragma.externalwebclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceTokenProvider {

    @Value("${jwt.service-token}")
    private String serviceToken;

    public String getServiceToken() {
        return "Bearer " + serviceToken;
    }
}