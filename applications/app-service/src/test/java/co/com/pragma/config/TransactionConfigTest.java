package co.com.pragma.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.ReactiveTransactionManager;

import static org.junit.jupiter.api.Assertions.*;

class TransactionConfigTest {

    @Test
    void testTransactionBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {

            ReactiveTransactionManager txManager = context.getBean(ReactiveTransactionManager.class);
            TransactionalOperator txOperator = context.getBean(TransactionalOperator.class);

            assertNotNull(txManager, "ReactiveTransactionManager bean not found in context.");
            assertNotNull(txOperator, "TransactionalOperator bean not found in context.");
        }
    }

    @Configuration
    @Import(TransactionConfig.class)
    static class TestConfig {

        @Bean
        public ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }
    }
}