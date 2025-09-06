package co.com.pragma.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostgreSQLConnectionPoolTest {

    private PostgreSQLConnectionPool connectionPool;

    private PostgreSQLConnectionPoolProperties poolProperties;
    private PostgresqlConnectionProperties properties;

    @BeforeEach
    void setUp() {
        poolProperties = new PostgreSQLConnectionPoolProperties(1, 10, 5);

        properties = new PostgresqlConnectionProperties(
                "localhost",
                5432,
                "dbName",
                "schema",
                "username",
                "password"
        );

        connectionPool = new PostgreSQLConnectionPool(poolProperties);
    }

    @Test
    void getConnectionConfigSuccess() {
        ConnectionPool pool = connectionPool.getConnectionConfig(properties);
        assertNotNull(pool);
    }
}
