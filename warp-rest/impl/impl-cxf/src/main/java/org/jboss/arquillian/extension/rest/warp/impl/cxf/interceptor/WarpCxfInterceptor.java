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
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static org.jboss.arquillian.extension.rest.warp.impl.cxf.interceptor.CxfContextBuilder.buildContext;

/**
 * CXF interceptor. This class implements {@link RequestHandler} and {@link ResponseHandler} in order to capture the
 * execution state within the server. <p/> Implementation captures the state and stores it the {@link RestContext} which
 * is being bound to executing request.
 *
 * <p><strong>Thread-safety:</strong>This class can be considered as a thread safe. The class is mutable, but since it's
 * using {@link ThreadLocal} field for storing it's context it can be considered as a thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@Provider
public class WarpCxfInterceptor implements RequestHandler, ResponseHandler {

    /**
     * The message context.
     */
    @Context
    private MessageContext messageContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleRequest(Message message, ClassResourceInfo classResourceInfo) {

        // captures the request message
        buildContext(messageContext.getHttpServletRequest())
                .setMessageContext(messageContext)
                .setRequestMessage(message)
                .build();

        // returns null, indicating that the request should be proceeded
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleResponse(Message message, OperationResourceInfo operationResourceInfo, Response response) {

        // sets the response
        buildContext(messageContext.getHttpServletRequest())
                .setMessageContext(messageContext)
                .setResponseMessage(message)
                .setResponse(response)
                .build();

        // returns null, indicating that the request should be proceeded
        return null;
    }
}
