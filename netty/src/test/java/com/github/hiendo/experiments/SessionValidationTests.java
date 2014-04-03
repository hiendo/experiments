package com.github.hiendo.experiments;

import com.github.hiendo.experiments.httpserver.SpringBootAppConfiguration;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.hamcrest.core.StringContains;
import org.springframework.boot.SpringApplication;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.hamcrest.MatcherAssert.assertThat;

public class SessionValidationTests {

    private HttpProxyServer httpProxyServer;

    @BeforeSuite(alwaysRun = true)
    public void beforeMethod() throws Exception {
        httpProxyServer = new HttpProxyServer(8080);
        httpProxyServer.start();

        SpringApplication app = new SpringApplication(SpringBootAppConfiguration.class);
        app.run();
    }

    @AfterSuite(alwaysRun = true)
    public void afterMethod() throws Exception {
        if (httpProxyServer != null) {
            httpProxyServer.stop();
        }
    }

    @Test
    public void validSessionCanBeProxied() throws Exception {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget webTarget = client.target("http://localhost:8080");

        String response = webTarget.request().header("Authorization", "secret").get(String.class);
        assertThat("response", response, StringContains.containsString("Hello World"));

        response = webTarget.request().header("Authorization", "secret").get(String.class);
        assertThat("response", response, StringContains.containsString("Hello World"));
    }


    @Test
    public void invalidSessionShouldNotBeProxied() throws Exception {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget webTarget = client.target("http://localhost:8080");

        try {
            webTarget.request().header("Authorization", "badsecret").get(String.class);
            Assert.fail("Expected error");
        } catch (ProcessingException e) {}

        try {
            webTarget.request().header("NoAuthorizationHeader", "blank").get(String.class);
            Assert.fail("Expected error");
        } catch (ProcessingException e) {}

    }
}
