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
package org.jboss.arquillian.quickstart.jersey.service;

import org.jboss.arquillian.quickstart.jersey.model.Stock;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * A REST service contract.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface StockService {

    /**
     * Creates new stock.
     *
     * @param stock the stock to create
     *
     * @return the response
     */
    Response createStock(Stock stock);

    /**
     * Updates the stock.
     *
     * @param id    stock id
     * @param stock the stock to update
     */
    void updateStock(@PathParam("id") long id, Stock stock);

    /**
     * Retrieves the stock by it's id.
     *
     * @param id the stock id
     *
     * @return the stock that matches the given id
     */
    Stock getStock(@PathParam("id") long id);

    /**
     * Retrieves the stock list.
     *
     * @param startIndex the starting index
     * @param size       the number stocks to retrieve
     *
     * @return the list of stocks
     */
    List<Stock> getStocks(@DefaultValue("0") @QueryParam("startIndex") int startIndex, @DefaultValue("10") @QueryParam("size") int size);

    /**
     * Deletes the stock
     *
     * @param id the stock id
     *
     * @return the response
     */
    Response deleteStock(@PathParam("id") long id);
}
