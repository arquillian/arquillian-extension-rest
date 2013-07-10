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
 * HTTP response that were send by the server back to the client. The response contains the http status code, the mime
 * type of the response and gives access to the entity that has been returned from the service.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface HttpResponse {

    /**
     * Retrieves the status code.
     *
     * @return the status code
     */
    int getStatusCode();

    /**
     * Retrieves the content type.
     *
     * @return the content type
     */
    String getContentType();

    /**
     * Retrieves the entity.
     *
     * @return the entity
     */
    Object getEntity();

    /**
     * Retrieves the request http headers.
     *
     * @return the request http headers
     */
    MultivaluedMap<String, String> getHeaders();
}
