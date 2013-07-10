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
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import static org.jboss.arquillian.extension.rest.warp.impl.jersey.integration.JerseyContextBuilder.buildContext;

/**
 * Jersey interceptor. This class implements {@link ContainerRequestFilter} and {@link ContainerResponseFilter} in order
 * to capture the execution state within the server.
 * <p/>
 * Implementation captures the state and stores it the {@link RestContext} which is being bound to executing request.
 *
 * <p><strong>Thread-safety:</strong>This class can be considered as a thread safe. The class is mutable, but since
 * it's using {@link ThreadLocal} field for storing it's context it can be considered as a thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@Provider
public class WarpJerseyInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Injected {@link HttpServletRequest}.
     *
     * Note: according to the jersey spec this should be a thread local copy of the request.
     * Setter injection is not supported, this is the only way to enquire the {@link HttpServletRequest}.
     */
    @Context
    private HttpServletRequest request;

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        // stores the container request
        buildContext(request)
                .setContainerRequest(containerRequest)
                .build();

        // returns the result
        return containerRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {

        // stores the container request and response
        buildContext(request)
                .setContainerRequest(containerRequest)
                .setContainerResponse(containerResponse)
                .build();

        // returns the result
        return containerResponse;
    }
}
