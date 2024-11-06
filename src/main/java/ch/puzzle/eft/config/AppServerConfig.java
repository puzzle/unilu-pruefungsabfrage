package ch.puzzle.eft.config;

import java.net.InetSocketAddress;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppServerConfig {

    private static final String PROTOCOL = "AJP/1.3";

    @Value("${server.ajp.protocol:https}")
    private String ajpProtocol;
    @Value("${server.ajp.port:8448}")
    private int ajpPort;
    @Value("${server.ajp.packet.size:65536}")
    private int ajpPacketSize;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory tomcat) {
                tomcat.addAdditionalTomcatConnectors(redirectConnector());
            }
        };
    }

    private Connector redirectConnector() {
        Connector connector = new Connector(PROTOCOL);
        connector.setScheme(ajpProtocol);
        connector.setPort(ajpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);

        AbstractAjpProtocol protocol = (AbstractAjpProtocol) connector.getProtocolHandler();
        protocol.setSecretRequired(false);
        protocol.setTomcatAuthentication(false);
        protocol.setAllowedRequestAttributesPattern(".{1,}"); // should be "AJP_"
        protocol.setPacketSize(ajpPacketSize);
        protocol.setAddress(new InetSocketAddress(0).getAddress());

        return connector;
    }
}