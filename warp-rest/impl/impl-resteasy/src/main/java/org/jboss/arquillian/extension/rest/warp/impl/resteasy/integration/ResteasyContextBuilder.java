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
package org.jboss.arquillian.extension.rest.warp.impl.resteasy.integration;

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
import org.jboss.resteasy.core.ServerResponse;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * The RestEasy specific {@link RestContext} builder.
 * <p>
 * <p><strong>Thread-safety:</strong>This class is mutable and not thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
final class ResteasyContextBuilder implements RestContextBuilder {

    /**
     * Represents the name of the attribute used for storing the builder in the context.
     */
    private static final String BUILDER_ATTRIBUTE_NAME = ResteasyContextBuilder.class.getName();

    /**
     * Represents the http request.
     */
    private final org.jboss.resteasy.spi.HttpRequest httpRequest;

    /**
     * Represents the build rest context.
     */
    private final RestContextImpl restContext;

    /**
     * The entity that has been send to the service.
     */
    private Object requestEntity;

    /**
     * The response content type.
     */
    private MediaType responseMediaType;

    /**
     * Resteasy server response.
     */
    private ServerResponse serverResponse;

    /**
     * Represents the security context.
     */
    private javax.ws.rs.core.SecurityContext securityContext;

    /**
     * <p>Creates new instance of {@link ResteasyContextBuilder}.</p>
     *
     * @param httpRequest
     *     the http request
     *
     * @throws IllegalArgumentException
     *     if httpRequest is null
     */
    private ResteasyContextBuilder(org.jboss.resteasy.spi.HttpRequest httpRequest) {

        Validate.notNull(httpRequest, "The 'httpRequest' can not be null.");

        this.httpRequest = httpRequest;
        this.restContext = getRestContext();
    }

    /**
     * The utility method that creates new instance of {@link ResteasyContextBuilder}.
     *
     * @param httpRequest
     *     the http request
     *
     * @return the created builder instance
     *
     * @throws IllegalArgumentException
     *     if servletRequest is null
     */
    public static ResteasyContextBuilder buildContext(org.jboss.resteasy.spi.HttpRequest httpRequest) {

        return getRestContextBuilder(httpRequest);
    }

    /**
     * Sets the response content type.
     *
     * @param responseMediaType
     *     the response content type
     *
     * @return the rest context builder
     */
    public ResteasyContextBuilder setResponseMediaType(MediaType responseMediaType) {

        this.responseMediaType = responseMediaType;
        return this;
    }

    /**
     * Sets the sever response.
     *
     * @param serverResponse
     *     the server response
     *
     * @return the rest context builder
     */
    public ResteasyContextBuilder setServerResponse(ServerResponse serverResponse) {

        this.serverResponse = serverResponse;
        return this;
    }

    /**
     * Sets the request entity.
     *
     * @param entity
     *     the request entity
     *
     * @return the rest context builder
     */
    public ResteasyContextBuilder setRequestEntity(Object entity) {

        this.requestEntity = entity;
        return this;
    }

    /**
     * Sets the security context
     *
     * @param securityContext
     *     the security context
     *
     * @return the rest context builder
     */
    public ResteasyContextBuilder setSecurityContext(javax.ws.rs.core.SecurityContext securityContext) {

        this.securityContext = securityContext;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build() {

        restContext.setHttpRequest(buildHttpRequest());
        restContext.setHttpResponse(buildHttpResponse());
        restContext.setResponse(serverResponse);
        restContext.setSecurityContext(buildSecurityContext());
    }

    /**
     * Builds the {@link HttpRequest}.
     *
     * @return the {@link HttpRequest}
     */
    private HttpRequest buildHttpRequest() {

        HttpRequestImpl request = new HttpRequestImpl();
        if (httpRequest != null) {
            request.setContentType(getMediaTypeName(httpRequest.getHttpHeaders().getMediaType()));
            request.setEntity(this.requestEntity);
            request.setMethod(getHttpMethod(httpRequest.getHttpMethod()));
            request.setHeaders(new MultivaluedMapImpl<String, String>(httpRequest.getHttpHeaders().getRequestHeaders()));
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
        if (serverResponse != null) {
            response.setContentType(getMediaTypeName(responseMediaType));
            response.setStatusCode(serverResponse.getStatus());
            response.setEntity(serverResponse.getEntity());
            response.setHeaders(serverResponse.getMetadata());
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
        if (this.securityContext != null) {
            securityContext.setPrincipal(this.securityContext.getUserPrincipal());
            securityContext.setAuthenticationScheme(this.securityContext.getAuthenticationScheme());
        }
        return securityContext;
    }

    /**
     * Retrieves the content type or null if it is not set.
     *
     * @param mediaType
     *     the media type
     *
     * @return the content type/mime name
     */
    private static String getMediaTypeName(MediaType mediaType) {

        return mediaType != null ? mediaType.toString() : null;
    }

    /**
     * Maps the http method name into corresponding {@link HttpMethod}.
     *
     * @param methodName
     *     the http method name
     *
     * @return the {@link HttpMethod}
     */
    private static HttpMethod getHttpMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }

    /**
     * Returns list of http headers values.
     *
     * @param values
     *     the list of values
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
     * @return the {@link ResteasyContextBuilder} instance
     */
    private static ResteasyContextBuilder getRestContextBuilder(org.jboss.resteasy.spi.HttpRequest httpRequest) {

        ResteasyContextBuilder resteasyContextBuilder = (ResteasyContextBuilder)
            httpRequest.getAttribute(BUILDER_ATTRIBUTE_NAME);

        if (resteasyContextBuilder == null) {

            resteasyContextBuilder = new ResteasyContextBuilder(httpRequest);
            httpRequest.setAttribute(BUILDER_ATTRIBUTE_NAME, resteasyContextBuilder);
        }

        return resteasyContextBuilder;
    }

    /**
     * Retrieves the {@link RestContext} stored in the request.
     * <p/>
     * If non exists, then new one is being created.
     *
     * @return the rest context
     */
    private RestContextImpl getRestContext() {

        RestContextImpl restContext = (RestContextImpl) httpRequest.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE);

        if (restContext == null) {

            restContext = new RestContextImpl();
            httpRequest.setAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
        }

        return restContext;
    }
}
