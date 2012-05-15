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
package org.jboss.arquillian.extension.rest.app;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.extension.rest.app.model.Customer;

/**
 * A REST service for retrieving Customer records
 */
@ManagedBean
@Path("/customer")
public class CustomerResource {
    @Inject
    private EntityManager em;

    @GET @Produces(MediaType.APPLICATION_XML)
    public List<Customer> getAllCustomers() {
       return findAllCustomers();
    }

    /**
     * This method responds to a GET request that supports the content type application/xml or application/json and returns the
     * requested customer resource.
     *
     * <p>
     * The customer is retrieved by id. A representation of the customer is then written into the response in the requested
     * format. The id value is taken from the final path segment.
     * </p>
     *
     * <a class="citation" href= "javacode://com.acme.jaxrs.CustomerResource#getCustomerById(java.lang.String)" />
     */
    @GET @Produces(MediaType.APPLICATION_XML)
    @Path("/{id:[1-9][0-9]*}")
    public Customer getCustomerById(@PathParam("id") long id) {
        System.out.println("Handling resource request for /customer/" + id);
        return findCustomerById(id);
    }

    private List<Customer> findAllCustomers() {
        CriteriaQuery<Customer> criteria = em.getCriteriaBuilder().createQuery(Customer.class);
        criteria.from(Customer.class);
        return em.createQuery(criteria).getResultList();
    }

    private Customer findCustomerById(long id) {
        return em.find(Customer.class, id);
    }
}
