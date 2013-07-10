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
package org.jboss.arquillian.extension.rest.warp.impl.client;

import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestInterceptorEnricher;
import org.jboss.arquillian.warp.spi.WarpDeploymentEnrichmentExtension;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link WarpRestExtension} class.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WarpRestExtensionTestCase extends AbstractTestTestBase {

    /**
     * Represents the instance of tested class.
     */
    private WarpRestExtension instance;

    /**
     * Represents the service loader.
     */
    @Mock
    private ServiceLoader serviceLoader;

    /**
     * Represents the mock instance of {@link LoadableExtension.ExtensionBuilder}.
     */
    @Mock
    private LoadableExtension.ExtensionBuilder builder;

    /**
     * Represents the mock instance of {@link WarpRestInterceptorEnricher}.
     */
    @Mock
    private WarpRestInterceptorEnricher interceptorEnricher;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {

        instance = new WarpRestExtension();
        getManager().inject(instance);

        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
        when(serviceLoader.all(WarpRestInterceptorEnricher.class))
                .thenReturn(Collections.singleton(interceptorEnricher));
    }

    /**
     * Registers extensions.
     *
     * @param extensions the list of extensions
     */
    @Override
    protected void addExtensions(List<Class<?>> extensions) {

        // given
        extensions.add(WarpRestExtension.class);
    }

    /**
     * Tests the {@link WarpRestExtension#register(LoadableExtension.ExtensionBuilder)} method.
     */
    @Test
    public void shouldRegisterExtension() {

        // when
        instance.register(builder);

        // then
        verify(builder).service(WarpDeploymentEnrichmentExtension.class, WarpRestExtension.class);
    }

    /**
     * Tests the {@link WarpRestExtension#getEnrichmentLibrary()} method.
     */
    @Test
    public void shouldCreateEnrichmentDeployment() {

        // when
        JavaArchive archive = instance.getEnrichmentLibrary();

        // then
        assertThat(archive).isNotNull();
    }

    /**
     * Tests the {@link WarpRestExtension#enrichWebArchive(WebArchive)} method.
     */
    @Test
    public void shouldEnrichWebArchives() {

        // given
        WebArchive webArchive = mock(WebArchive.class);

        // when
        instance.enrichWebArchive(webArchive);

        // then
        verify(interceptorEnricher).enrichWebArchive(webArchive);
    }
}
