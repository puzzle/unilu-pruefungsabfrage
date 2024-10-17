package ch.puzzle.eft.config;

import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {"server.ajp.protocol=https", "server.ajp.port=8484", "server.ajp.packet.size=676767"})
class AppServerConfigIT {

    @Autowired
    private TomcatServletWebServerFactory factory;

    @Test
    public void testAjpConnectorProperties() {
        List<Connector> ajpConnectorList = factory.getAdditionalTomcatConnectors()
                                                  .stream()
                                                  .filter(c -> c.getProtocol()
                                                                .toLowerCase()
                                                                .contains("ajp"))
                                                  .toList();

        assertNotEquals(0, ajpConnectorList.size(), "One AJP connector should be registered");
        assertTrue(ajpConnectorList.size() < 2, "Not more than one AJP connector should be registered");

        Connector ajpConnector = ajpConnectorList.getFirst();

        assertEquals("AJP/1.3", ajpConnector.getProtocol());
        assertEquals("https", ajpConnector.getScheme());
        assertEquals(8484, ajpConnector.getPort());
        assertFalse(ajpConnector.getSecure());
        assertFalse(ajpConnector.getAllowTrace());

        AbstractAjpProtocol<?> protocol = (AbstractAjpProtocol<?>) ajpConnector.getProtocolHandler();
        assertFalse(protocol.getSecretRequired());
        assertFalse(protocol.getTomcatAuthentication());
        assertEquals(".{1,}", protocol.getAllowedRequestAttributesPattern());
        assertEquals(676767, protocol.getPacketSize());
    }
}
