package org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.client;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration.WarpJaxrs2Interceptor;
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
 * Tests the {@link WarpJaxrs2InterceptorEnricher} class.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WarpJaxrs2InterceptorEnricherTestCase {

    /**
     * Represents the instance of tested class.
     */
    private WarpJaxrs2InterceptorEnricher instance;

    /**
     * Represents the mock instance of {@link LoadableExtension.ExtensionBuilder}.
     */
    @Mock
    private LoadableExtension.ExtensionBuilder builder;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {

        instance = new WarpJaxrs2InterceptorEnricher();
    }

    /**
     * Tests the {@link WarpJaxrs2InterceptorEnricher#register(LoadableExtension.ExtensionBuilder)} method.
     */
    @Test
    public void shouldRegisterExtension() {

        // when
        instance.register(builder);

        // then
        verify(builder).service(WarpRestInterceptorEnricher.class, WarpJaxrs2InterceptorEnricher.class);
    }

    /**
     * Tests the {@link WarpJaxrs2InterceptorEnricher#enrichWebArchive(WebArchive)} method.
     */
    @Test
    public void shouldEnrichWebArchives() {

        // given
        WebArchive webArchive = mock(WebArchive.class);

        // when
        instance.enrichWebArchive(webArchive);

        // then
        verify(webArchive).addPackage(WarpJaxrs2Interceptor.class.getPackage());
    }
}
