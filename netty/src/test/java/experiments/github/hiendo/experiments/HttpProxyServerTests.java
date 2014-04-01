package experiments.github.hiendo.experiments;

import com.github.hiendo.experiments.HttpProxyServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.hamcrest.core.StringContains;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.hamcrest.MatcherAssert.assertThat;

public class HttpProxyServerTests {

    private HttpProxyServer httpProxyServer;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws Exception {
        httpProxyServer = new HttpProxyServer(8080);
        httpProxyServer.start();

        /**
         * @todo: create http server here so we don't depend on google.com
         */
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() throws Exception {
        if (httpProxyServer != null) {
            httpProxyServer.stop();
        }
    }

    @Test
    public void canBeProxied() throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.ASYNC_THREADPOOL_SIZE, "5");
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget webTarget = client.target("http://localhost:8080");

        String response = webTarget.request().header("authCode", "validated").get(String.class);
        assertThat("response", response, StringContains.containsString("Google Search"));

        try {
            webTarget.request().get(String.class);
            Assert.fail("Expected error");
        } catch (ProcessingException e) {}
    }
}
