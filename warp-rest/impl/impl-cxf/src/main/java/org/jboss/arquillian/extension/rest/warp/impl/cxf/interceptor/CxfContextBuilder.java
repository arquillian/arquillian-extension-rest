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
package org.jboss.arquillian.extension.rest.warp.impl.cxf.interceptor;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.message.Message;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The CXF specific {@link RestContext} builder.
 *
 * <p><strong>Thread-safety:</strong>This class is mutable and not thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
final class CxfContextBuilder implements RestContextBuilder {

    /**
     * Represents the name of the attribute used for storing the builder in the context.
     */
    private static final String BUILDER_ATTRIBUTE_NAME = CxfContextBuilder.class.getName();

    /**
     * Represents the servlet request.
     */
    private final ServletRequest servletRequest;

    /**
     * Represents the rest context
     */
    private final RestContextImpl restContext;

    /**
     * Represents the request message.
     */
    private Message requestMessage;

    /**
     * Represents the response message.
     */
    private Message responseMessage;

    /**
     * Represents the response.
     */
    private Response response;

    /**
     * Represents the message context.
     */
    private MessageContext messageContext;

    /**
     * Creates new instance of {@link CxfContextBuilder} class.
     *
     * @param servletRequest the servlet request
     */
    private CxfContextBuilder(ServletRequest servletRequest) {
        Validate.notNull(servletRequest, "The 'servletRequest' can not be null.");

        this.servletRequest = servletRequest;
        this.restContext = getRestContext();
    }

    /**
     * The utility method that creates new instance of {@link CxfContextBuilder}.
     *
     * @param servletRequest the servlet request
     *
     * @return the created builder instance
     *
     * @throws IllegalArgumentException if servletRequest is null
     */
    public static CxfContextBuilder buildContext(ServletRequest servletRequest) {

        return getCxfContextBuilder(servletRequest);
    }

    /**
     * Sets the request message
     *
     * @param requestMessage the request message
     *
     * @return the context builder
     */
    public CxfContextBuilder setRequestMessage(Message requestMessage) {

        this.requestMessage = requestMessage;
        return this;
    }

    /**
     * Sets the response message.
     *
     * @param responseMessage the response message
     *
     * @return the context builder
     */
    public CxfContextBuilder setResponseMessage(Message responseMessage) {

        this.responseMessage = responseMessage;
        return this;
    }

    /**
     * Sets the response.
     *
     * @param response the response
     *
     * @return the context builder
     */
    public CxfContextBuilder setResponse(Response response) {

        this.response = response;
        return this;
    }

    /**
     * Sets the message context.
     *
     * @param messageContext the message context
     */
    public CxfContextBuilder setMessageContext(MessageContext messageContext) {

        this.messageContext = messageContext;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build() {

        restContext.setHttpRequest(buildHttpRequest());
        restContext.setHttpResponse(buildHttpResponse());
        restContext.setResponse(response);
        restContext.setSecurityContext(buildSecurityContext());
    }

    /**
     * Builds the http request.
     *
     * @return the http request
     */
    @SuppressWarnings("unchecked")
    private HttpRequest buildHttpRequest() {

        HttpRequestImpl request = new HttpRequestImpl();
        if (requestMessage != null) {
            request.setContentType((String) requestMessage.get(Message.CONTENT_TYPE));
            request.setEntity(getRequestEntity());
            request.setMethod(getRequestMethod((String) requestMessage.get(Message.HTTP_REQUEST_METHOD)));
            request.setHeaders(getHeaders((Map<String, List<String>>) requestMessage.get(Message.PROTOCOL_HEADERS)));
        }

        return request;
    }

    /**
     * Builds the http response.
     *
     * @return the http response
     */
    @SuppressWarnings("unchecked")
    private HttpResponse buildHttpResponse() {

        HttpResponseImpl response = new HttpResponseImpl();

        if (this.response != null) {
            response.setContentType((String) responseMessage.get(Message.CONTENT_TYPE));
            response.setStatusCode(this.response.getStatus());
            response.setEntity(this.response.getEntity());
            response.setHeaders(getHeaders((Map<String, List<String>>)
                    this.responseMessage.get(Message.PROTOCOL_HEADERS)));
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

        if(messageContext != null && messageContext.getSecurityContext() != null) {
            securityContext.setPrincipal(messageContext.getSecurityContext().getUserPrincipal());
            securityContext.setAuthenticationScheme(messageContext.getSecurityContext().getAuthenticationScheme());
        }
        return securityContext;
    }

    /**
     * Retrieves the request entity.
     *
     * @return the request entity
     */
    public Object getRequestEntity() {

        return requestMessage.getContentFormats().size() > 0 ?
                requestMessage.getContent(requestMessage.getContentFormats().iterator().next()) : null;
    }

    /**
     * Maps the http method name to {@link HttpMethod}.
     *
     * @param methodName the method name
     *
     * @return the {@link HttpMethod}
     */
    private HttpMethod getRequestMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }

    /**
     * Maps the headers object value map into simple string representation.
     *
     * @param httpHeaders the http headers map
     *
     * @return the result map
     */
    private MultivaluedMap<String, String> getHeaders(Map<String, List<String>> httpHeaders) {

        if(httpHeaders == null) {
            return null;
        }

        MultivaluedMap<String, String> result = new MultivaluedMapImpl<String, String>();
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            result.put(entry.getKey(), getHeaderValueList(entry.getValue()));
        }
        return result;
    }

    /**
     * Returns list of http headers values.
     *
     * @param values the list of values
     *
     * @return the list of values
     */
    private List<String> getHeaderValueList(List<String> values) {

        List<String> result = new ArrayList<String>();
        for (String val : values) {
            result.add(val);
        }
        return result;
    }

    /**
     * Retrieves the builder from the request.
     *
     * @return the {@link CxfContextBuilder} instance
     */
    private static CxfContextBuilder getCxfContextBuilder(ServletRequest servletRequest) {

        CxfContextBuilder cxfContextBuilder = (CxfContextBuilder)
                servletRequest.getAttribute(BUILDER_ATTRIBUTE_NAME);

        if(cxfContextBuilder == null) {

            cxfContextBuilder = new CxfContextBuilder(servletRequest);
            servletRequest.setAttribute(BUILDER_ATTRIBUTE_NAME, cxfContextBuilder);
        }

        return cxfContextBuilder;
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
