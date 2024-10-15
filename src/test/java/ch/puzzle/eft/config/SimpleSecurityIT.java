package ch.puzzle.eft.config;

import java.util.Collections;
import javax.net.ssl.SSLContext;

import ch.puzzle.eft.ExamFeedbackToolApplication;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("for local integration tests only with running SP")
@ActiveProfiles("test")
@SpringBootTest(classes = ExamFeedbackToolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SimpleSecurityIT {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSecurityIT.class);
    private static final String BASE_URL = "https://edview-test.unilu.ch";

    @Value("${client.ssl.trust-store}")
    private Resource trustStore;

    @Value("${client.ssl.trust-store-password}")
    private String trustStorePassword;

    @Test
    void callHomeUrl() throws Exception {
        ResponseEntity<String> response = restTemplate().getForEntity(BASE_URL + "/home",
                                                                      String.class,
                                                                      Collections.emptyMap());

        logger.info(response.getBody());
        assertTrue(response.getBody()
                           .contains("<h1>Welcome Home!</h1>"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void startLoginProcessForAuthorizedMethod() throws Exception {
        ResponseEntity<String> response = restTemplate().getForEntity(BASE_URL + "/search",
                                                                      String.class,
                                                                      Collections.emptyMap());

        logger.info(response.getBody());
        assertTrue(response.getBody()
                           .contains("This is the Discovery Service for the AAI Test federation."));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void keepRunningServer() {
        keepRunningServer(false, "exam-feedback-tool", 1000);
    }

    RestTemplate restTemplate() throws Exception {
        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                                                                                                 .build();
        HttpClient httpClient = HttpClients.custom()
                                           .setConnectionManager(connectionManager)
                                           .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    // use local truststore instead of Java keystore cacerts
    RestTemplate restTemplateUsingLocalTruststore() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore.getURL(),
                                                                          trustStorePassword.toCharArray())
                                                       .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                                                                                                 .setSSLSocketFactory(socketFactory)
                                                                                                 .build();
        HttpClient httpClient = HttpClients.custom()
                                           .setConnectionManager(connectionManager)
                                           .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    private static void keepRunningServer(boolean keepRunning, String serverName, int seconds) {
        // run application server for the next seconds...
        if (keepRunning) {
            logger.info("Keep {} running", serverName);
            for (int i = 0; i < seconds; i++) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.info("{} is stopping", serverName);
        }
        logger.info("{} is stopped", serverName);
    }
}
