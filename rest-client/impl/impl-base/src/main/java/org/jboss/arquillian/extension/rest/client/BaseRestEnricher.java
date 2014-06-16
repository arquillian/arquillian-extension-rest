/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.extension.rest.client;

import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestEnricher;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseRestEnricher implements TestEnricher {

    @Inject
    private Instance<ProtocolMetaData> metaDataInst;

    @Inject
    private Instance<Response> responseInst;

    @Override
    public void enrich(Object testCase)
    {
    }

    @Override
    public Object[] resolve(Method method)
    {
        Object[] values = new Object[method.getParameterTypes().length];
        Class<?>[] parameterTypes = method.getParameterTypes();
        final Consumes consumes = method.getDeclaringClass().getAnnotation(Consumes.class);
        final Produces produces = method.getDeclaringClass().getAnnotation(Produces.class);
        if (responseInst.get() != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (Response.class.isAssignableFrom(parameterTypes[i])) {
                    values[i] = responseInst.get();
                }
            }
        } else {
            for (int i = 0; i < parameterTypes.length; i++) {
                final Annotation[] parameterAnnotations = method.getParameterAnnotations()[i];
                for (Annotation annotation : parameterAnnotations) {
                    if (annotation instanceof ArquillianResteasyResource) {
                        ArquillianResteasyResource arr = (ArquillianResteasyResource) annotation;
                        Class<?> clazz = parameterTypes[i];
                        if (isSupportedParameter(clazz)) {
                            values[i] = enrichByType(clazz, method, arr, consumes, produces);
                        } else {
                            throw new RuntimeException("Not able to provide a client injection for type " + clazz);
                        }
                    }
                }
            }
        }
        return values;
    }

    private void addHeaders(Map<String, String> headersMap, Header annotation)
    {
        if (null != annotation) {
            headersMap.put(annotation.name(), annotation.value());
        }
    }

    protected boolean allInSameContext(List<Servlet> servlets)
    {
        Set<String> context = new HashSet<String>();
        for (Servlet servlet : servlets) {
            context.add(servlet.getContextRoot());
        }
        return context.size() == 1;
    }

    protected abstract Object enrichByType(Class<?> clazz, Method method, ArquillianResteasyResource annotation, Consumes consumes, Produces produces);

    // Currently no way to share @ArquillianResource URL (URLResourceProvider) logic internally, copied logic
    protected URI getBaseURL()
    {
        HTTPContext context = metaDataInst.get().getContext(HTTPContext.class);
        if (allInSameContext(context.getServlets())) {
            return context.getServlets().get(0).getBaseURI();
        }
        throw new IllegalStateException("No baseURL found in HTTPContext");
    }

    protected Map<String, String> getHeaders(Class<?> clazz, Method method)
    {
        final Map<String, String> headers = getHeaders(clazz);
        headers.putAll(getHeaders(method));
        return headers;
    }

    protected Map<String, String> getHeaders(AnnotatedElement annotatedElement)
    {
        final Map<String, String> headersMap = new HashMap<String, String>();
        final Headers headersAnnotation = annotatedElement.getAnnotation(Headers.class);
        if (null != headersAnnotation && null != headersAnnotation.value()) {
            for (Header header : headersAnnotation.value()) {
                addHeaders(headersMap, header);
            }
        }
        addHeaders(headersMap, annotatedElement.getAnnotation(Header.class));

        return headersMap;
    }

    protected abstract boolean isSupportedParameter(Class<?> clazz);
}
