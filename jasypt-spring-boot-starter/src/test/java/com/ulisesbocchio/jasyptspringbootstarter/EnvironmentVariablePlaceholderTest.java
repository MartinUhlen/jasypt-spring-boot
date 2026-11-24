package com.ulisesbocchio.jasyptspringbootstarter;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression test: on Spring Boot 4.2 an environment variable whose value holds a property placeholder made the
 * context fail with a StackOverflowError (the system environment configuration property cache was rebuilt
 * endlessly while being built, see DefaultPropertyResolver).
 */
@ExtendWith(SystemStubsExtension.class)
@SpringBootTest(classes = EnvironmentVariablePlaceholderTest.TestConfig.class,
        properties = "spring.application.name=placeholder-test")
public class EnvironmentVariablePlaceholderTest {

    @SystemStub
    private EnvironmentVariables environmentVariables = new EnvironmentVariables(
            "TEST_ENV_WITH_PLACEHOLDER", "app=${spring.application.name:fallback}",
            "TEST_ENV_WITH_ESCAPED_PLACEHOLDER", "$${not.a.property}");

    @Autowired
    private Environment environment;

    @EnableEncryptableProperties
    public static class TestConfig {
    }

    @Test
    public void contextLoads_andPlaceholderInEnvironmentVariableIsResolved() {
        assertEquals("app=placeholder-test", environment.getProperty("TEST_ENV_WITH_PLACEHOLDER"));
        assertEquals("app=placeholder-test", environment.getProperty("test.env.with.placeholder"));
    }
}
