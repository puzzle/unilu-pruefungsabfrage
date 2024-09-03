package ch.puzzle.eft.e2e;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.startupcheck.IndefiniteWaitOneShotStartupCheckStrategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CypressE2ETest {
    private static final Logger logger = LoggerFactory
            .getLogger(CypressE2ETest.class);


    @LocalServerPort
    private int port;

    @Test
    void runCypressE2ETests() {
        Testcontainers
                .exposeHostPorts(port);
        try (GenericContainer<?> container = new GenericContainer<>("cypress/included:13.13.2")) {
            container
                    .withClasspathResourceMapping("e2e", "/e2e", BindMode.READ_ONLY)
                    .withWorkingDirectory("/e2e")
                    .withEnv("CYPRESS_baseUrl", "http://host.testcontainers.internal:" + port)
                    .withLogConsumer(new Slf4jLogConsumer(logger))
                    .withStartupCheckStrategy(new IndefiniteWaitOneShotStartupCheckStrategy())
                    .withCommand("--browser=chrome --headed")
                    .start();
            assertThat(container
                    .getLogs())
                    .contains("All specs passed!");
        }
    }
}