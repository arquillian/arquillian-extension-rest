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

import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestCommons;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link RestContextProvider} class.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class RestContextProviderTestCase extends AbstractTestTestBase {

    /**
     * Represents the instance of tested class.
     */
    private RestContextProvider instance;

    /**
     * Represents the http servlet request.
     */
    @Mock
    private HttpServletRequest servletRequest;

    /**
     * Represents the instance of {@link ArquillianResource}.
     */
    @Mock
    private ArquillianResource arquillianResource;

    /**
     * Represents the instance of {@link RestContext}.
     */
    @Mock
    private RestContext restContext;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {

        instance = new RestContextProvider();
        getManager().inject(instance);

        bind(ApplicationScoped.class, HttpServletRequest.class, servletRequest);
        when(servletRequest.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE)).thenReturn(restContext);
    }

    /**
     * Registers extensions.
     *
     * @param extensions the list of extensions
     */
    @Override
    protected void addExtensions(List<Class<?>> extensions) {

        extensions.add(RestContextProvider.class);
    }

    /**
     * Tests the {@link RestContextProvider#canProvide(Class)} method.
     */
    @Test
    public void shouldSupportRestContext() {

        // when
        boolean result = instance.canProvide(RestContext.class);

        // then
        assertThat(result).isTrue();
    }

    /**
     * Tests the {@link RestContextProvider#canProvide(Class)} method.
     */
    @Test
    public void shouldNotSupportOtherTypes() {

        // when
        boolean result = instance.canProvide(Object.class);

        // then
        assertThat(result).isFalse();
    }

    /**
     * Tests the {@link RestContextProvider#lookup(ArquillianResource, java.lang.annotation.Annotation...)}.
     */
    @Test
    public void shouldLookupRestContext() {

        // when
        RestContext result = (RestContext) instance.lookup(arquillianResource);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(restContext);
    }

    /**
     * Tests the {@link RestContextProvider#lookup(ArquillianResource, java.lang.annotation.Annotation...)}.
     * <p />
     * {@link RestContextNotFoundException} is expected.
     */
    @Test(expected = RestContextNotFoundException.class)
    public void shouldThrowRestContextNotFoundException() {

        // given
        when(servletRequest.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE)).thenReturn(null);

        // when
        instance.lookup(arquillianResource);
    }
}
