/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.extension.rest;

import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.app.CustomerResource;
import org.jboss.arquillian.extension.rest.app.CustomerResourceImpl;
import org.jboss.arquillian.extension.rest.app.model.Customer;
import org.jboss.arquillian.extension.rest.app.persistence.EntityManagerProducer;
import org.jboss.arquillian.extension.rest.app.rs.JaxRsActivator;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.extension.rest.client.ClassModifier;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Arquillian Extension REST API Test Case
 * <p/>
 * Annotate the TestClass's TestMethods with JAX-RS Client annotations.
 * <p/>
 * Executes the REST request in the background for so to inject back the Response into the TestMethods arguments.
 * <p/>
 * * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 *
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class RestClientTestCase {

    @ArquillianResource
    private URL deploymentURL;

    @Deployment(testable = false)
    public static WebArchive create()
    {
        return ShrinkWrap.create(WebArchive.class)
            .addPackage(Customer.class.getPackage())
            .addClasses(EntityManagerProducer.class, CustomerResource.class, CustomerResourceImpl.class, JaxRsActivator.class)
            .addAsResource("import.sql")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * Arquillian by default substitutes overlaping annotations from injected resource methods (all methods) with those from test method.
     * If you need to modify one method in one way and another differently then you must create separate interface.
     *
     * @param webTarget webTarget, configured for an appropriate endpoint.
     */
    @Test
    public void getAllCustomersWithCustomInterface(@ArquillianResteasyResource("rest") WebTarget webTarget)
    {
//        Given
        Invocation.Builder builder = webTarget.path("/customer").request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

//        When
        List<Customer> result = builder.get(List.class);

//        Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCustomerById(@ArquillianResteasyResource Client client)
    {
//        Given
        final String name = "Acme Corporation";
        final long customerId = 1L;

        Invocation.Builder builder = client.target(deploymentURL+"rest").path("/customer/{customerId}").resolveTemplate("customerId",customerId)
                .request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML);
//        When
        final Customer result = builder.get(Customer.class);

//        Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());
    }

    @Test
    public void getCustomerById(@ArquillianResteasyResource ClientBuilder clientBuilder)
    {
//        Given
        final String name = "Acme Corporation";
        final long customerId = 1L;

        Invocation.Builder builder = clientBuilder.build()
                .target(deploymentURL+"rest").path("/customer/{customerId}").resolveTemplate("customerId",customerId)
                .request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML);
//        When
        final Customer result = builder.get(Customer.class);

//        Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());
    }


}