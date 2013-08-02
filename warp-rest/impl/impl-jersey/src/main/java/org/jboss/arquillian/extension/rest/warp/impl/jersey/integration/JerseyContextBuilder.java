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
package org.jboss.arquillian.extension.rest.warp.impl.jersey.integration;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import org.jboss.arquillian.container.spi.client.deployment.Validate;
import org.jboss.arquillian.extension.rest.warp.api.HttpMethod;
import org.jboss.arquillian.extension.rest.warp.api.HttpRequest;
import org.jboss.arquillian.extension.rest.warp.api.HttpResponse;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.extension.rest.warp.api.SecurityContext;
import org.jboss.arquillian.extension.rest.warp.spi.HttpRequestImpl;
import org.jboss.arquillian.extension.rest.warp.spi.HttpResponseImpl;
import org.jboss.arquillian.extension.rest.warp.spi.MultivaluedMapImpl;
import org.jboss.arquillian.extension.rest.warp.spi.RestContextBuilder;
import org.jboss.arquillian.extension.rest.warp.spi.RestContextImpl;
import org.jboss.arquillian.extension.rest.warp.spi.SecurityContextImpl;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestCommons;

import javax.servlet.ServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Jersey specific {@link RestContext} builder.
 *
 * <p><strong>Thread-safety:</strong>This class is mutable and not thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
final class JerseyContextBuilder implements RestContextBuilder {

    /**
     * Represents the name of the attribute used for storing the builder in the context.
     */
    private static final String BUILDER_ATTRIBUTE_NAME = JerseyContextBuilder.class.getName();

    /**
     * Represents the servlet request.
     */
    private final ServletRequest servletRequest;

    /**
     * Represents the rest context.
     */
    private final RestContextImpl restContext;

    /**
     * Represents the container request.
     */
    private ContainerRequest containerRequest;

    /**
     * Represents the container response.
     */
    private ContainerResponse containerResponse;

    /**
     * Creates new instance of {@link JerseyContextBuilder} class.
     *
     * @param servletRequest the servlet request
     *
     * @throws IllegalArgumentException if servlet request is null
     */
    private JerseyContextBuilder(ServletRequest servletRequest) {
        Validate.notNull(servletRequest, "The 'servletRequest' can not be null.");

        this.servletRequest = servletRequest;
        this.restContext = getRestContext();
    }

    /**
     * The utility method that creates new instance of {@link JerseyContextBuilder}.
     *
     * @param servletRequest the servlet request
     *
     * @return the created builder instance
     *
     * @throws IllegalArgumentException if servletRequest is null
     */
    public static JerseyContextBuilder buildContext(ServletRequest servletRequest) {

        return getJerseyContextBuilder(servletRequest);
    }

    /**
     * Sets the container request.
     *
     * @param containerRequest the container request
     *
     * @return the rest context builder
     */
    public JerseyContextBuilder setContainerRequest(ContainerRequest containerRequest) {

        this.containerRequest = containerRequest;
        return this;
    }

    /**
     * Sets the container response
     *
     * @param containerResponse the container response
     *
     * @return the rest context builder
     */
    public JerseyContextBuilder setContainerResponse(ContainerResponse containerResponse) {

        this.containerResponse = containerResponse;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build() {

        restContext.setHttpRequest(buildHttpRequest());
        restContext.setHttpResponse(buildHttpResponse());
        restContext.setResponse(containerResponse != null ? containerResponse.getResponse() : null);
        restContext.setSecurityContext(buildSecurityContext());
    }

    /**
     * Builds the {@link HttpRequest}.
     *
     * @return the {@link HttpRequest}
     */
    private HttpRequest buildHttpRequest() {

        HttpRequestImpl request = new HttpRequestImpl();
        if (request != null) {
            request.setContentType(getMediaTypeName(containerRequest.getMediaType()));
            // TODO accessing the request entity in jersey is bizarre
            // and requires knowing it's type up front, which is not possible at the current stage
            // request.setEntity(containerRequest.getEntity(Object.class));
            request.setMethod(getHttpMethod(containerRequest.getMethod()));
            request.setHeaders(new MultivaluedMapImpl<String, String>(containerRequest.getRequestHeaders()));
        }
        return request;
    }

    /**
     * Builds the {@link HttpResponse}.
     *
     * @return the {@link HttpResponse}
     */
    private HttpResponse buildHttpResponse() {

        HttpResponseImpl response = new HttpResponseImpl();

        if (containerResponse != null) {
            response.setContentType(getMediaTypeName(containerResponse.getMediaType()));
            response.setStatusCode(containerResponse.getStatus());
            response.setEntity(containerResponse.getEntity());
            response.setHeaders(containerResponse.getHttpHeaders());
        }

        return response;
    }

    /**
     * Builds the {@link SecurityContext}.
     *
     * @return the {@link SecurityContext}
     */
    private SecurityContext buildSecurityContext() {

        SecurityContextImpl securityContext = new SecurityContextImpl();

        if(containerRequest != null) {
            securityContext.setPrincipal(containerRequest.getUserPrincipal());
            securityContext.setAuthenticationScheme(containerRequest.getAuthenticationScheme());
        }
        return securityContext;
    }

    /**
     * Retrieves the content mime type name.
     *
     * @param mediaType the content mime type
     *
     * @return the content mime type name
     */
    private String getMediaTypeName(MediaType mediaType) {
        return mediaType != null ? mediaType.toString() : null;
    }

    /**
     * Maps the http method name into correspondng {@link HttpMethod}.
     *
     * @param methodName the method name
     *
     * @return the {@link HttpMethod}
     */
    private static HttpMethod getHttpMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }

    /**
     * Returns list of http headers values.
     *
     * @param values the list of values
     *
     * @return the list of values
     */
    private List<String> getHttpValueList(List<Object> values) {

        List<String> result = new ArrayList<String>();
        for (Object val : values) {
            result.add(val.toString());
        }
        return result;
    }

    /**
     * Retrieves the builder from the request.
     *
     * @return the {@link JerseyContextBuilder} instance
     */
    private static JerseyContextBuilder getJerseyContextBuilder(ServletRequest servletRequest) {

        JerseyContextBuilder jerseyContextBuilder = (JerseyContextBuilder)
                servletRequest.getAttribute(BUILDER_ATTRIBUTE_NAME);

        if(jerseyContextBuilder == null) {

            jerseyContextBuilder = new JerseyContextBuilder(servletRequest);
            servletRequest.setAttribute(BUILDER_ATTRIBUTE_NAME, jerseyContextBuilder);
        }

        return jerseyContextBuilder;
    }

    /**
     * Retrieves the {@link RestContext} stored in the request. <p/> If non exists, then new one is being created.
     *
     * @return the rest context
     */
    private RestContextImpl getRestContext() {

        RestContextImpl restContext = (RestContextImpl) servletRequest.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE);

        if (restContext == null) {

            restContext = new RestContextImpl();
            servletRequest.setAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
        }

        return restContext;
    }
}
