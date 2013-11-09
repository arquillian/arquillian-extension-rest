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

import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ProxyBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * RestEnricher
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class RestEnricher extends BaseRestEnricher implements TestEnricher {

    @Override
    protected boolean isSupportedParameter(Class<?> clazz) {
        return true;  //proxy based, as a result always supported
    }

    @Override
    protected Object enrichByType(Class<?> clazz, Method method, ArquillianResteasyResource annotation, Consumes consumes, Produces produces) {
        Object value;
        final String resourcePath = annotation.value();
        if (ClientRequest.class.isAssignableFrom(clazz)) {
            value = new ClientRequestFactory(getBaseURL()).createRelativeRequest(resourcePath);
        } else {
            final Class<?> parameterType;
            try {
                final Annotation[] methodDeclaredAnnotations = method.getDeclaredAnnotations();
//                                This is test method so if it only contains @Test annotation then we don't need to hassel with substitutions
                parameterType = methodDeclaredAnnotations.length <= 1 ? clazz : ClassModifier.getModifiedClass(clazz,
                        methodDeclaredAnnotations);
            } catch (Exception e) {
                throw new RuntimeException("Cannot substitute annotations for method " + method.getName(), e);
            }
            final ProxyBuilder<?> proxyBuilder = ProxyBuilder.build(parameterType, getBaseURL() + resourcePath);
            if (null != consumes && consumes.value().length > 0) {
                proxyBuilder.serverConsumes(MediaType.valueOf(consumes.value()[0]));
            }
            if (null != produces && produces.value().length > 0) {
                proxyBuilder.serverProduces(MediaType.valueOf(produces.value()[0]));
            }
            value = proxyBuilder.now();
        }
        return value;
    }
}
