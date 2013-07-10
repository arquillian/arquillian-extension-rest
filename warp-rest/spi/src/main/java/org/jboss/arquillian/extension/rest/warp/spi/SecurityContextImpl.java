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
package org.jboss.arquillian.extension.rest.warp.spi;

import org.jboss.arquillian.extension.rest.warp.api.SecurityContext;

import java.security.Principal;

/**
 * The default implementation of {@link SecurityContext}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class SecurityContextImpl implements SecurityContext {

    /**
     * Represents the principal.
     */
    private Principal principal;

    /**
     * Represents the authentication scheme.
     */
    private String authenticationScheme;

    /**
     * Creates new instance of {@link SecurityContext} class.
     */
    public SecurityContextImpl() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Sets the principal
     *
     * @param principal the principal
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    /**
     * Sets the authentication scheme.
     *
     * @param authenticationScheme the authentication scheme
     */
    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }
}
