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

/**
 * Exception used for indicating that the {@link org.jboss.arquillian.extension.rest.warp.api.RestContext}
 * hasn't been found for the given test case.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 * @see org.jboss.arquillian.extension.rest.warp.api.RestContext
 */
public class RestContextNotFoundException extends RuntimeException {

    /**
     * Creates new instance of {@link RestContextNotFoundException} exception with detailed error message.
     *
     * @param message
     *     the detailed error message
     */
    public RestContextNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates new instance of {@link RestContextNotFoundException} exception with detailed error message and inner
     * cause.
     *
     * @param message
     *     the detailed error message
     * @param cause
     *     the inner cause
     */
    public RestContextNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
