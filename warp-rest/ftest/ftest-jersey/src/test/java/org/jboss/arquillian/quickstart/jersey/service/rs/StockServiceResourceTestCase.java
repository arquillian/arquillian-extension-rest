/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.quickstart.jersey.service.rs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.quickstart.jersey.model.Stock;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.extension.rest.warp.api.HttpMethod;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * The test case that uses Jersey client API for calling the REST test.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class StockServiceResourceTestCase {

    /**
     * Creates the test deployment.
     *
     * @return the test deployment
     */
    @Deployment
    @OverProtocol("Servlet 3.0")
    public static Archive createTestArchive() {

        return Deployments.createDeployment();
    }

    /**
     * The context path of the deployed application.
     */
    @ArquillianResource
    private URL contextPath;

    /**
     * Represents the REST service client.
     */
    private Client client;

    /**
     * <p>Sets up the test environment.</p>
     */
    @Before
    public void setUp() {

        client = Client.create(new DefaultClientConfig());
    }

    @Test
    @RunAsClient
    public void testStockCreate() {

        WebResource webResource = client.resource(contextPath + "rest/stocks");

        ClientResponse response = webResource
            .accept(MediaType.APPLICATION_XML_TYPE)
            .type(MediaType.APPLICATION_XML_TYPE)
            .post(ClientResponse.class, createStock());

        assertEquals("The request didn't succeeded.", Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    public void testStockGet() {

        Stock stock = createStock();

        client.resource(contextPath + "rest/stocks")
            .accept(MediaType.APPLICATION_XML_TYPE)
            .type(MediaType.APPLICATION_XML_TYPE)
            .post(ClientResponse.class, stock);

        WebResource webResource = client.resource(contextPath + "rest/stocks/2");

        Stock result = webResource
            .accept(MediaType.APPLICATION_XML_TYPE)
            .get(Stock.class);

        assertEquals("Stock has invalid name.", stock.getName(), result.getName());
        assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
        assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
    }

    @Test
    @RunAsClient
    public void testStockGetWarp() {

        final Stock stock = createStock();

        client.resource(contextPath + "rest/stocks")
            .accept(MediaType.APPLICATION_XML_TYPE)
            .type(MediaType.APPLICATION_XML_TYPE)
            .post(ClientResponse.class, stock);

        Warp.initiate(new Activity() {
            @Override
            public void perform() {

                WebResource webResource = client.resource(contextPath + "rest/stocks/2");

                Stock result = webResource
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(Stock.class);

                assertEquals("Stock has invalid name.", stock.getName(), result.getName());
                assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
                assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
            }
        }).inspect(new Inspection() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStock() {

                assertEquals(HttpMethod.GET, restContext.getHttpRequest().getMethod());
                assertEquals(200, restContext.getHttpResponse().getStatusCode());
                assertEquals("application/xml", restContext.getHttpResponse().getContentType());
            }
        });
    }

    /**
     * Creates the instance of {@link Stock} for testing
     *
     * @return the created stock instance
     */
    private Stock createStock() {

        Stock stock = new Stock();
        stock.setName("Acme");
        stock.setCode("ACM");
        stock.setValue(new BigDecimal(127D));
        stock.setDate(new Date());
        return stock;
    }
}
