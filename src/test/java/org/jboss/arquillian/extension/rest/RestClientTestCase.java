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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.app.CustomerResource;
import org.jboss.arquillian.extension.rest.app.model.Customer;
import org.jboss.arquillian.extension.rest.app.persistence.EntityManagerProducer;
import org.jboss.arquillian.extension.rest.app.rs.JaxRsActivator;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian Extension REST API Test Case
 *
 * Annotate the TestClass's TestMethods with JAX-RS Client annotations.
 *
 * Executes the REST request in the background for so to inject back the Response into the TestMethods arguments.  
 *
 * * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class RestClientTestCase
{
   @Deployment(testable = false)
   public static WebArchive create() {
      return ShrinkWrap.create(WebArchive.class)
            .addPackage(Customer.class.getPackage())
            .addClasses(EntityManagerProducer.class, CustomerResource.class, JaxRsActivator.class)
            .addAsResource("import.sql")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Test @GET @Path("rest/customer") @Consumes(MediaType.APPLICATION_XML)
   public void shouldBeAbleToListAllCustomers(ClientResponse<List<Customer>> response)
   {
      Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());

      List<Customer> customers = response.getEntity(new GenericType<List<Customer>>(){});
      Assert.assertEquals(2, customers.size());
   }

   @Test @GET @Path("rest/customer/1") @Consumes(MediaType.APPLICATION_XML)
   public void shouldBeAbleToListACustomer(ClientResponse<Customer> response)
   {
      Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());

      Customer customer = response.getEntity(Customer.class);
      Assert.assertEquals(1, customer.getId().intValue());
   }
}