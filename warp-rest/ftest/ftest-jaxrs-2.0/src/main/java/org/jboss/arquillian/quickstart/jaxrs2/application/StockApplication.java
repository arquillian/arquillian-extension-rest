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
package org.jboss.arquillian.quickstart.jaxrs2.application;

import org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration.WarpJaxrs2Interceptor;
import org.jboss.arquillian.quickstart.jaxrs2.service.rs.StockServiceResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The stock application.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@ApplicationPath("/rest")
public class StockApplication extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<?>> getClasses() {
        // TODO the WARP interceptor needs to be added explicitly, due to the fact
        // that this deployment uses explicit dispatcher servlet
        return Collections.<Class<?>>singleton(WarpJaxrs2Interceptor.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Object> getSingletons() {
        return Collections.<Object>singleton(new StockServiceResource());
    }
}
