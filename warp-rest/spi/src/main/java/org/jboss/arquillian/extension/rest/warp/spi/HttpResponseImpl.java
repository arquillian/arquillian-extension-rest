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

import org.jboss.arquillian.extension.rest.warp.api.HttpResponse;

import javax.ws.rs.core.MultivaluedMap;

/**
 * The default implementation of {@link HttpResponse}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class HttpResponseImpl implements HttpResponse {

    /**
     * Represents the status code.
     */
    private int statusCode;

    /**
     * Represents the content type.
     */
    private String contentType;

    /**
     * Represents the response entity.
     */
    private Object entity;

    /**
     * Represents the http headers.
     */
    private MultivaluedMap<String, String> headers;

    /**
     * Creates new instance of {@link HttpResponseImpl} class.
     */
    public HttpResponseImpl() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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
     * Sets the response entity.
     *
     * @param entity the response entity
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
