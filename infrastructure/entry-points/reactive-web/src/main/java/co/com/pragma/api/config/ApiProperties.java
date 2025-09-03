package co.com.pragma.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("adapters.reactive-web")
public record ApiProperties(
        String basePath) {
}

