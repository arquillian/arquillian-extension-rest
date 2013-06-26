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
     * @param customerResource configured resource ready for use, injected by Arquillian
     */
    @Test
    public void getAllCustomersWithCustomInterface(@ArquillianResteasyResource("rest") CustomCustomerResource customerResource)
    {
//        Given

//        When
        final List<Customer> result = customerResource.getAllCustomers();

//        Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Arquillian calculates resource path by using deployment URL+ArquillianResteasyResource.value which is by default "rest".
     * If your API is located under different root i.e. "api_v2" then use @ArquillianResteasyResource("api_v2")
     *
     * @param customerResource configured resource ready for use, injected by Arquillian
     */
    @Test
    public void getCustomerById(@ArquillianResteasyResource("rest") CustomerResource customerResource)
    {
//        Given
        final String name = "Acme Corporation";
        final long customerId = 1L;

//        When
        final Customer result = customerResource.getCustomerById(customerId);

//        Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());
    }

    /**
     * CustomerResource.createCustomer is annotated with @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}).
     * This means that injected proxy by default will use first mime type which in this case is JSON. To force proxy to use XML we annotate
     * test method with @Produces(MediaType.APPLICATION_XML).
     * Arquillian scanns all CustomerResource methods and substitutes annotations with those from this test method.
     * CustomerResource is annotated also with @POST and @Path which will remain, because they don't exist on this test method.
     *
     * @param customerResource configured resource ready for use, injected by Arquillian
     */
    @Test
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public void createCustomer(@ArquillianResteasyResource CustomerResource customerResource)
    {
//        Given
        final String name = "Jack";

//        When
        final Customer result = customerResource.createCustomer(new Customer(name));

//        Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());
    }

    /**
     * We can inject either proxy or a ResteasyWebTarget for low level manipulations and assertions.
     *
     * @param webTarget configured resource ready for use, injected by Arquillian
     */
    @Test
    public void createPackageBareRsource(@ArquillianResteasyResource("rest/customer") ResteasyWebTarget webTarget)
    {
//        Given
        final Invocation.Builder invocationBuilder = webTarget.request();
        final Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Customer(), MediaType.APPLICATION_JSON_TYPE));

//        When
        final Response response = invocation.invoke();

//        Then
        assertEquals(deploymentURL + "rest/customer", webTarget.getUri().toASCIIString());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    /**
     * Totally manually created RestEasy client. This test shows what crazy thing you can do manually with ClassModifier.
     *
     * @throws Exception well, test may throw exceptions from time to time
     */
    @Test
    public void manualClassModifierUsage() throws Exception
    {
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_XML)
        class A {

        }
        final Class<CustomerResource> aClass = ClassModifier.getModifiedClass(CustomerResource.class, A.class.getAnnotations());
//        Given
        final String name = "Jack";
        final Customer customer = new Customer(name);
        Client client = ResteasyClientBuilder.newClient();
        WebTarget webTarget = client.target(deploymentURL + "rest");
        ResteasyWebTarget resteasyWebTarget = (ResteasyWebTarget) webTarget;
        final CustomerResource customerResource = resteasyWebTarget.proxy(aClass);

//        When
        final Customer result = customerResource.createCustomer(customer);

//        Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());
    }

    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/customer")
    public static interface CustomCustomerResource {

        /**
         * CustomerResource.getAllCustomers is annotated with<ul>
         * <li>Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})</li>
         * </ul>
         * By default proxy would use first mime type. We want returned response to be in JSON instead of XML.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON})
        List<Customer> getAllCustomers();

        /**
         * CustomerResource.createCustomer is annotated with<ul>
         * <li>Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})</li>
         * <li>Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})</li>
         * </ul>
         * By default proxy would use first mime type. We want returned response to be in XML instead of JSON.
         */
        @POST
        @Produces({MediaType.APPLICATION_XML})
        @Path("/")
        Customer createCustomer(Package pkg);
    }
}