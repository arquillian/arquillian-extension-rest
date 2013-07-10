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

import javax.ws.rs.core.Response;

/**
 * Represents the captured rest execution context. Gives access to the http request and response allowing to verify the
 * response statuses or retrieve the request/response entity. The rest context can be injected into Warp {@link
 * Inspection} by using {@link ArquillianResource} injection. It's possible to access the service response through
 * {@link #getResponse()}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 * @see HttpRequest
 * @see HttpResponse
 * @see ArquillianResource
 */
public interface RestContext {

    /**
     * Retrieves the http request.
     *
     * @return the http request
     */
    HttpRequest getHttpRequest();

    /**
     * Retrieves the http response
     *
     * @return the http response
     */
    HttpResponse getHttpResponse();

    /**
     * Retrieves the service response.
     *
     * @return the service response
     */
    Response getResponse();

    /**
     * Retrieves the security context.
     *
     * @return security context
     */
    SecurityContext getSecurityContext();
}
