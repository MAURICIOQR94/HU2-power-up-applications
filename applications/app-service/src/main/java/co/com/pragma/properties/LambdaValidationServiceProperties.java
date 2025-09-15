package co.com.pragma.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.lambda-validation")
public record LambdaValidationServiceProperties(
        String baseUrl) {
}
