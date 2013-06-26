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
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RestEnricher
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class RestEnricher implements TestEnricher {

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
        if (responseInst.get() != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (Response.class.isAssignableFrom(parameterTypes[i])) {
                    values[i] = responseInst.get();
                }
            }
        } else {
            final Consumes consumes = method.getDeclaringClass().getAnnotation(Consumes.class);
            final Produces produces = method.getDeclaringClass().getAnnotation(Produces.class);
            for (int i = 0; i < parameterTypes.length; i++) {
                final Annotation[] parameterAnnotations = method.getParameterAnnotations()[i];
                for (Annotation annotation : parameterAnnotations) {
                    if (annotation instanceof ArquillianResteasyResource) {
                        Client client = ResteasyClientBuilder.newClient();
                        WebTarget webTarget = client.target(getBaseURL() + ((ArquillianResteasyResource) annotation).value());
                        ResteasyWebTarget resteasyWebTarget = (ResteasyWebTarget) webTarget;
                        if (ResteasyWebTarget.class.isAssignableFrom(parameterTypes[i])) {
                            values[i] = resteasyWebTarget;
                        } else {
                            final Class<?> parameterType;
                            try {
                                final Annotation[] methodDeclaredAnnotations = method.getDeclaredAnnotations();
//                                This is test method so if it only contains @Test annotation then we don't need to hassel with substitutions
                                parameterType = methodDeclaredAnnotations.length <= 1 ? parameterTypes[i] : ClassModifier.getModifiedClass(parameterTypes[i],
                                    methodDeclaredAnnotations);
                            } catch (Exception e) {
                                throw new RuntimeException("Cannot substitute annotations for method " + method.getName(), e);
                            }
                            final ProxyBuilder<?> proxyBuilder = resteasyWebTarget.proxyBuilder(parameterType);
                            if (null != consumes && consumes.value().length > 0) {
                                proxyBuilder.defaultConsumes(consumes.value()[0]);
                            }
                            if (null != produces && produces.value().length > 0) {
                                proxyBuilder.defaultProduces(produces.value()[0]);
                            }
                            values[i] = proxyBuilder.build();
                        }
                    }
                }
            }
        }
        return values;
    }

    private boolean allInSameContext(List<Servlet> servlets)
    {
        Set<String> context = new HashSet<String>();
        for (Servlet servlet : servlets) {
            context.add(servlet.getContextRoot());
        }
        return context.size() == 1;
    }

    // Currently no way to share @ArquillianResource URL (URLResourceProvider) logic internally, copied logic
    private URI getBaseURL()
    {
        HTTPContext context = metaDataInst.get().getContext(HTTPContext.class);
        if (allInSameContext(context.getServlets())) {
            return context.getServlets().get(0).getBaseURI();
        }
        throw new IllegalStateException("No baseURL found in HTTPContext");
    }
}
