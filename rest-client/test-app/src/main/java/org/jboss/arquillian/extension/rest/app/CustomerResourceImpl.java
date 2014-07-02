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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST service for retrieving Customer records
 */
public class CustomerResourceImpl implements CustomerResource {

    private static final String CUSTOMERS_ATTRIBUTE_NAME = "customers";

    private static final String CUSTOMER_ID_SEQUENCE_ATTRIBUTE_NAME = "customerIdSequence";

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public List<Customer> getAllCustomers()
    {
        return findAllCustomers();
    }

    /**
     * This method responds to a GET request that supports the content type application/xml or application/json and returns the
     * requested customer resource.
     * <p/>
     * <p>
     * The customer is retrieved by id. A representation of the customer is then written into the response in the requested
     * format. The id value is taken from the final path segment.
     * </p>
     * <p/>
     * <a class="citation" href= "javacode://com.acme.jaxrs.CustomerResource#getCustomerById(java.lang.String)" />
     */
    @Override
    public Customer getCustomerById(@PathParam("id") long id)
    {
        return findCustomerById(id);
    }

    @Override
    public Customer banCustomer(long id)
    {
        final Customer customer = findCustomerById(id);
        if (null == customer) {
            return null;
        }
        customer.setBanned(true);
        return customer;
    }

    @Override
    public Customer createCustomer(Customer customer)
    {
        customer.setId(nextId());
        return customer;
    }

    private List<Customer> findAllCustomers()
    {
        final Object attribute = httpServletRequest.getAttribute(CUSTOMERS_ATTRIBUTE_NAME);
        List<Customer> customers;
        if (!(attribute instanceof List)) {
            customers = new ArrayList<Customer>();
            customers.add(new Customer(nextId(), "Acme Corporation"));
            customers.add(new Customer(nextId(), "Don"));
            httpServletRequest.setAttribute(CUSTOMERS_ATTRIBUTE_NAME, customers);
        } else {
            //noinspection unchecked
            customers = (List<Customer>) attribute;
        }
        return customers;
    }

    private Customer findCustomerById(long id)
    {
        for (Customer customer : findAllCustomers()) {
            if (id == customer.getId()) {
                return customer;
            }
        }
        return null;
    }

    private long nextId()
    {
        final Object attribute = httpServletRequest.getAttribute(CUSTOMER_ID_SEQUENCE_ATTRIBUTE_NAME);
        long newValue;
        if (attribute instanceof Long) {
            newValue = ((Long) attribute) + 1;
        } else {
            newValue = 1;
        }
        httpServletRequest.setAttribute(CUSTOMER_ID_SEQUENCE_ATTRIBUTE_NAME, newValue);
        return newValue;
    }
}
