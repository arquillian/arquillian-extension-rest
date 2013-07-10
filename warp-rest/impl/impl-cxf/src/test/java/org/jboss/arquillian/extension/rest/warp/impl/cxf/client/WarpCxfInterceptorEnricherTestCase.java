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
package org.jboss.arquillian.extension.rest.warp.impl.cxf.client;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.extension.rest.warp.impl.cxf.interceptor.WarpCxfInterceptor;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestInterceptorEnricher;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link WarpCxfInterceptorEnricher} class.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WarpCxfInterceptorEnricherTestCase {

    /**
     * Represents the instance of tested class.
     */
    private WarpCxfInterceptorEnricher instance;

    /**
     * Represents the mock instance of {@link org.jboss.arquillian.core.spi.LoadableExtension.ExtensionBuilder}.
     */
    @Mock
    private LoadableExtension.ExtensionBuilder builder;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {

        instance = new WarpCxfInterceptorEnricher();
    }

    /**
     * Tests the {@link WarpCxfInterceptorEnricher#register(LoadableExtension.ExtensionBuilder)} method.
     */
    @Test
    public void shouldRegisterExtension() {

        // when
        instance.register(builder);

        // then
        verify(builder).service(WarpRestInterceptorEnricher.class, WarpCxfInterceptorEnricher.class);
    }

    /**
     * Tests the {@link WarpCxfInterceptorEnricher#enrichWebArchive(org.jboss.shrinkwrap.api.spec.WebArchive)} method.
     */
    @Test
    public void shouldEnrichWebArchives() {

        // given
        WebArchive webArchive = mock(WebArchive.class);

        // when
        instance.enrichWebArchive(webArchive);

        // then
        verify(webArchive).addPackage(WarpCxfInterceptor.class.getPackage());
    }
}
