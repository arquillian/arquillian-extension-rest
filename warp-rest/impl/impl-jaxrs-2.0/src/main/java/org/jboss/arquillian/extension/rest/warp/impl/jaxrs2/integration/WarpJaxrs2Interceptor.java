package org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.InterceptorContext;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

import static org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration.Jaxrs2ContextBuilder.buildContext;

/**
 * JAX-RS 2.0 interceptor. This class implements {@link ContainerRequestFilter}, {@link ContainerResponseFilter}
 * {@link ReaderInterceptor} and {@link WriterInterceptor} in order to capture the execution state within the server.
 * <p/>
 * Implementation captures the state and stores it the {@link org.jboss.arquillian.extension.rest.warp.api.RestContext}
 * which is being bound to
 * executing request.
 *
 * <p><strong>Thread-safety:</strong>This class can be considered as a thread safe. The class is mutable, but since
 * it's using {@link ThreadLocal} field for storing it's context it can be considered as a thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@Provider
public class WarpJaxrs2Interceptor implements ContainerRequestFilter, ContainerResponseFilter, ReaderInterceptor,
        WriterInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        // captures the request
        buildContext(containerRequestContext)
                .setContainerRequestContext(containerRequestContext)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        // captures the response
        buildContext(containerRequestContext)
                .setContainerRequestContext(containerRequestContext)
                .setContainerResponseContext(containerResponseContext)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext readerInterceptorContext) throws IOException, WebApplicationException {

        // reads the request entity
        Object requestEntity = readerInterceptorContext.proceed();

        // stores the unmarshalled object
        buildContext(readerInterceptorContext)
                .setRequestEntity(requestEntity)
                .build();

        return requestEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {

        // proceeds with the context invocation
        writerInterceptorContext.proceed();

        // captures the response entity
        buildContext(writerInterceptorContext)
                .setResponseEntity(writerInterceptorContext.getEntity())
                .build();
    }
}
