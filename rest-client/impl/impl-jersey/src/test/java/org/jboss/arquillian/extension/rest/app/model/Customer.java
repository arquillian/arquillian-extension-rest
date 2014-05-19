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
package org.jboss.arquillian.extension.rest.app.model;

//import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Set;

/**
 * A <strong>Customer</strong> is an entity for whom orders are created.
 */
@Entity
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Set<Contact> contacts;

    private String name;

    private Set<SalesOrder> orders;

    public Customer()
    {
    }

    public Customer(String name)
    {
        this.name = name;
    }

    @Id
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    //    @JsonIgnore
    @XmlTransient
    @OneToMany(mappedBy = "customer")
    public Set<Contact> getContacts()
    {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts)
    {
        this.contacts = contacts;
    }

    @NotNull
    @Size(min = 3, max = 50)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    //    @JsonIgnore
    @XmlTransient
    @OneToMany(mappedBy = "customer")
    public Set<SalesOrder> getOrders()
    {
        return orders;
    }

    public void setOrders(Set<SalesOrder> orders)
    {
        this.orders = orders;
    }
}
