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

import org.jboss.arquillian.extension.rest.warp.api.HttpRequest;
import org.jboss.arquillian.extension.rest.warp.api.HttpResponse;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.extension.rest.warp.api.SecurityContext;

import javax.ws.rs.core.Response;

/**
 * The default implementation of {@link RestContext}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class RestContextImpl implements RestContext {

    /**
     * Represents the http request.
     */
    private HttpRequest httpRequest;

    /**
     * Represents the http response.
     */
    private HttpResponse httpResponse;

    /**
     * Represents the server response.
     */
    private Response response;

    /**
     * Represents the security context.
     */
    private SecurityContext securityContext;

    /**
     * Creates new instance of {@link RestContextImpl} class.
     */
    public RestContextImpl() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    /**
     * Sets the http request.
     *
     * @param httpRequest the http request
     */
    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    /**
     * Sets the http request
     *
     * @param httpResponse the http request
     */
    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response the response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    /**
     * Sets the security context.
     *
     * @param securityContext the security context
     */
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
}
