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

import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static org.jboss.arquillian.extension.rest.warp.impl.resteasy.integration.ResteasyContextBuilder.buildContext;

/**
 * RestEasy interceptor. This class implements {@link PreProcessInterceptor}, {@link PostProcessInterceptor} {@link
 * MessageBodyReaderInterceptor} and {@link MessageBodyWriterInterceptor} in order to capture the execution state within
 * the server.
 * <p/>
 * Implementation captures the state and stores it the {@link RestContext} which is being bound to
 * executing request.
 *
 * <p><strong>Thread-safety:</strong>This class can be considered as a thread safe. The class is mutable, but since
 * it's using {@link ThreadLocal} field for storing it's context it can be considered as a thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@Provider
@ServerInterceptor
public class WarpResteasyInterceptor implements PreProcessInterceptor, PostProcessInterceptor,
        MessageBodyReaderInterceptor, MessageBodyWriterInterceptor {

    /**
     * Stores the http request within the worker thread.
     */
    private static final ThreadLocal<HttpRequest> request = new ThreadLocal<HttpRequest>();

    /**
     * Stores the security context withing the worker thread.
     */
    private static final ThreadLocal<SecurityContext> securityContext = new ThreadLocal<SecurityContext>();

    /**
     * Sets the security context.
     *
     * @param securityContext the security context
     */
    @Context
    public void setSecurityContext(SecurityContext securityContext) {

        this.securityContext.set(securityContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethod resourceMethod) throws Failure, WebApplicationException {

        // stores the http request
        request.set(httpRequest);

        // initialize the context
        buildContext(request.get())
                .setSecurityContext(securityContext.get())
                .build();

        // returns null, does not overrides the original server response
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException {

        // reads the entity from the request
        Object result = context.proceed();

        // appends the entity to the context
        buildContext(request.get())
                .setRequestEntity(result)
                .build();

        // returns the entity for farther processing
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(ServerResponse serverResponse) {

        // captures the server response
        buildContext(request.get())
                .setServerResponse(serverResponse)
                .setSecurityContext(securityContext.get())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {

        // writes the response
        context.proceed();

        // retrieves the response content type
        buildContext(request.get())
                .setResponseMediaType(context.getMediaType())
                .build();
    }
}
