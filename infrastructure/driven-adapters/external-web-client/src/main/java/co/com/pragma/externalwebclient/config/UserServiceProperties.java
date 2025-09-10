package co.com.pragma.externalwebclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.driven-adapters.web-client.authentication")
public record UserServiceProperties (
        String baseUrl){
}
