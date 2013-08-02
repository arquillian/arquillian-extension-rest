package org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.client;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration.WarpJaxrs2Interceptor;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestInterceptorEnricher;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * The rest extension responsible for packaging JAX-RS 2.0 specific interceptors.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class WarpJaxrs2InterceptorEnricher implements LoadableExtension, WarpRestInterceptorEnricher {

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(LoadableExtension.ExtensionBuilder builder) {

        builder.service(WarpRestInterceptorEnricher.class, this.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichWebArchive(WebArchive archive) {

        archive.addPackage(WarpJaxrs2Interceptor.class.getPackage());
    }
}
