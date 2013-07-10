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
package org.jboss.arquillian.quickstart.jersey.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a stock.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@XmlRootElement(name = "stock")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stock {

    /**
     * The stock id.
     */
    @XmlAttribute
    private Long id;

    /**
     * The stock name.
     */
    @XmlElement
    private String name;

    /**
     * The stock three letter code.
     */
    @XmlElement
    private String code;

    /**
     * The stock valuation date.
     */
    @XmlElement
    private Date date;

    /**
     * The stock value.
     */
    @XmlElement
    private BigDecimal value;

    /**
     * Creates new instance of stock.
     */
    public Stock() {
        // empty constructor
    }

    /**
     * Retrieves the stock id.
     *
     * @return the stock id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the stock id.
     *
     * @param id the stock id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the stock name.
     *
     * @return the stock name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the stock name.
     *
     * @param name the stock name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the stock code.
     *
     * @return the stock code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the stock code.
     *
     * @param code the stock id
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Retrieves the stock valuation date.
     *
     * @return the stock valuation date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the stock valuation date.
     *
     * @param date the stock valuation date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrieves the stock value.
     *
     * @return the stock value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Retrieves the stock value.
     *
     * @param value the stock value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
