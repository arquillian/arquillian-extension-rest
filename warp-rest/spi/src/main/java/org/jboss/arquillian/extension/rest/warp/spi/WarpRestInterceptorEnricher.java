/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * An extension that is will be implemented by concreate implementations and allows it to enrich the test web archive
 * with required classes.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface WarpRestInterceptorEnricher {

    /**
     * Enriches the web archive by providing JAX-RS interceptor that will intercept execution context.
     *
     * @param archive the web archive to enrich
     */
    void enrichWebArchive(WebArchive archive);
}
