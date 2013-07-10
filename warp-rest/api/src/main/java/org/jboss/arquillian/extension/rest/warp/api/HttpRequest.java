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
package org.jboss.arquillian.extension.rest.warp.api;

import javax.ws.rs.core.MultivaluedMap;

/**
 * HTTP request that were received by the server.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface HttpRequest {

    /**
     * Retrieves the http method.
     *
     * @return the http method.
     */
    HttpMethod getMethod();

    /**
     * Retrieves the request content type.
     *
     * @return the request content type
     */
    String getContentType();

    /**
     * Retrieves the request entity.
     *
     * @return the request entity
     */
    Object getEntity();

    /**
     * Retrieves the request http headers.
     *
     * @return the request http headers
     */
    MultivaluedMap<String, String> getHeaders();
}
