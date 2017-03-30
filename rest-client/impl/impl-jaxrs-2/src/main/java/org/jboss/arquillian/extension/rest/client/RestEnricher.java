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

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * RestEnricher
 * Based on the existing REST Easy 3 client, but using pure JAX-RS 2.0 Client APIs and injection support.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="johndament@apache.org">John D. Ament</a>
 * @version $Revision: $
 */
public class RestEnricher extends BaseRestEnricher implements TestEnricher {

    @Override
    protected boolean isSupportedParameter(Class<?> clazz) {
        if (ClientBuilder.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Client.class.isAssignableFrom(clazz)) {
            return true;
        } else if (WebTarget.class.isAssignableFrom(clazz)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object enrichByType(Class<?> clazz, Method method, ArquillianResteasyResource annotation, Consumes consumes,
        Produces produces) {
        Object result = null;
        if (ClientBuilder.class.isAssignableFrom(clazz)) {
            result = ClientBuilder.newBuilder();
        } else if (Client.class.isAssignableFrom(clazz)) {
            result = ClientBuilder.newClient();
        } else if (WebTarget.class.isAssignableFrom(clazz)) {
            WebTarget webTarget = ClientBuilder.newClient().target(getBaseURL() + annotation.value());
            final Map<String, String> headers = getHeaders(clazz, method);
            if (!headers.isEmpty()) {
                webTarget.register(new HeaderFilter(headers));
            }
            result = webTarget;
        }
        return result;
    }
}
