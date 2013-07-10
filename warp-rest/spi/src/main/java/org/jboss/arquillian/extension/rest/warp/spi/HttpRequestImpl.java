/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.extension.rest.warp.spi;

import org.jboss.arquillian.extension.rest.warp.api.HttpMethod;
import org.jboss.arquillian.extension.rest.warp.api.HttpRequest;

import javax.ws.rs.core.MultivaluedMap;

/**
 * The default implementation of {@link HttpRequest}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class HttpRequestImpl implements HttpRequest {

    /**
     * Represents the http method.
     */
    private HttpMethod httpMethod;

    /**
     * Represents the content type.
     */
    private String contentType;

    /**
     * Represents the request entity.
     */
    private Object entity;

    /**
     * Represents the map of http headers.
     */
    private MultivaluedMap<String, String> headers;

    /**
     * Creates new instance of {@link HttpRequestImpl} class.
     */
    public HttpRequestImpl() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpMethod getMethod() {
        return httpMethod;
    }

    /**
     * Sets the http method.
     *
     * @param httpMethod the http method
     */
    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getEntity() {
        return entity;
    }

    /**
     * Sets the request entity.
     *
     * @param entity the request entity
     */
    public void setEntity(Object entity) {
        this.entity = entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the http headers.
     *
     * @param headers the http headers
     */
    public void setHeaders(MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }
}
