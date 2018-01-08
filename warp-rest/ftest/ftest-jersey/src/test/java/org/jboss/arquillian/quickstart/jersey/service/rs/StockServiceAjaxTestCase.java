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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.jboss.arquillian.warp.client.filter.http.HttpFilters.request;

/**
 * An Drone/Selenium test case that calls the service through AJAX.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class StockServiceAjaxTestCase {

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
     * The web driver instance.
     */
    @Drone
    WebDriver browser;

    @Test
    @RunAsClient
    public void testAjaxGetStocks() {

        Warp.initiate(new Activity() {

            @Override
            public void perform() {

                browser.navigate().to(contextPath + "restclient.jsp");
            }
        }).group().observe(request().uri().endsWith("/stocks")).inspect(new Inspection() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStocks() {

                assertThat(restContext.getHttpRequest().getMethod()).isEqualTo(HttpMethod.GET);
                assertThat(restContext.getHttpResponse().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
                assertThat(restContext.getHttpResponse().getContentType()).isEqualTo("application/json");

                List list = (List) restContext.getHttpResponse().getEntity();
                assertThat(list.size()).isEqualTo(1);
            }
        });
    }

    @Test
    @RunAsClient
    public void testAjaxGetStock() {

        browser.navigate().to(contextPath + "restclient.jsp");

        Warp.initiate(new Activity() {

            @Override
            public void perform() {

                browser.findElement(By.className("stockLink")).click();
            }
        }).group().observe(request().uri().endsWith("/stocks/1")).inspect(new Inspection() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStock() {

                assertThat(restContext.getHttpRequest().getMethod()).isEqualTo(HttpMethod.GET);
                assertThat(restContext.getHttpResponse().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
                assertThat(restContext.getHttpResponse().getContentType()).isEqualTo("application/json");

                Stock stock = (Stock) restContext.getHttpResponse().getEntity();
                assertThat(stock.getId()).isEqualTo(1L);
                assertThat(stock.getName()).isEqualTo("Acme");
                assertThat(stock.getCode()).isEqualTo("ACM");
            }
        });
    }
}
