package co.com.pragma.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.authentication")
public record AuthenticationServiceProperties(
        String baseUrl){
}
