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
package org.jboss.arquillian.extension.rest.warp.impl.provider;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestCommons;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 * Provider that allows to lookup the {@link RestContext}
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 * @see RestContext
 */
public class RestContextProvider implements ResourceProvider {

    /**
     * Instance of {@link HttpServletRequest}.
     */
    @Inject
    private Instance<HttpServletRequest> requestInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canProvide(Class<?> aClass) {

        return RestContext.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object lookup(ArquillianResource arquillianResource, Annotation... annotations) {

        // retrieves the http request
        HttpServletRequest request = requestInstance.get();
        // retrieves the rest context as a attribute from request
        RestContext restContext = (RestContext) request.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE);

        if(restContext == null) {

            throw new RestContextNotFoundException("The instance of RestContext can not be lookup."
                    + " Please check whether you had correctly configured interceptors");
        }

        return restContext;
    }
}
