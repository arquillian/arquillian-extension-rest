package org.jboss.arquillian.extension.rest.warp.impl.jaxrs2.integration;

import org.jboss.arquillian.container.spi.client.deployment.Validate;
import org.jboss.arquillian.extension.rest.warp.api.HttpMethod;
import org.jboss.arquillian.extension.rest.warp.api.HttpRequest;
import org.jboss.arquillian.extension.rest.warp.api.HttpResponse;
import org.jboss.arquillian.extension.rest.warp.api.SecurityContext;
import org.jboss.arquillian.extension.rest.warp.spi.HttpRequestImpl;
import org.jboss.arquillian.extension.rest.warp.spi.HttpResponseImpl;
import org.jboss.arquillian.extension.rest.warp.spi.MultivaluedMapImpl;
import org.jboss.arquillian.extension.rest.warp.spi.RestContextBuilder;
import org.jboss.arquillian.extension.rest.warp.spi.RestContextImpl;
import org.jboss.arquillian.extension.rest.warp.spi.SecurityContextImpl;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestCommons;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.InterceptorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The JAX-RS 2.0 specific {@link org.jboss.arquillian.extension.rest.warp.api.RestContext} builder.
 *
 * <p><strong>Thread-safety:</strong>This class is mutable and not thread safe.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public final class Jaxrs2ContextBuilder implements RestContextBuilder {

    /**
     * Represents the name of the attribute used for storing the builder in the context.
     */
    private static final String BUILDER_ATTRIBUTE_NAME = Jaxrs2ContextBuilder.class.getName();

    /**
     * Represents the servlet request.
     */
    private final ExecutionContext executionContext;

    /**
     * Represents the rest context.
     */
    private final RestContextImpl restContext;

    /**
     * Container request context.
     */
    private ContainerRequestContext containerRequestContext;

    /**
     * Container response context.
     */
    private ContainerResponseContext containerResponseContext;

    /**
     * Request entity.
     */
    private Object requestEntity;

    /**
     * Response entity.
     */
    private Object responseEntity;

    /**
     * Creates new instance of {@link Jaxrs2ContextBuilder} class.
     *
     * @param executionContext the execution context
     *
     * @throws IllegalArgumentException if servlet request is null
     */
    private Jaxrs2ContextBuilder(ExecutionContext executionContext) {
        Validate.notNull(executionContext, "The 'executionContext' can not be null.");

        this.executionContext = executionContext;
        this.restContext = getRestContext();
    }

    /**
     * The utility method that creates new instance of {@link Jaxrs2ContextBuilder}.
     *
     * @param containerRequestContext the request context
     *
     * @return the created builder instance
     *
     * @throws IllegalArgumentException if servletRequest is null
     */
    public static Jaxrs2ContextBuilder buildContext(ContainerRequestContext containerRequestContext) {

        return getJaxrsContextBuilder(new RequestExecutionContext(containerRequestContext));
    }

    /**
     * The utility method that creates new instance of {@link Jaxrs2ContextBuilder}.
     *
     * @param interceptorContext the interceptor context
     *
     * @return the created builder instance
     *
     * @throws IllegalArgumentException if servletRequest is null
     */
    public static Jaxrs2ContextBuilder buildContext(InterceptorContext interceptorContext) {

        return getJaxrsContextBuilder(new InterceptionExecutionContext(interceptorContext));
    }

    /**
     * Sets the container request context.
     *
     * @param containerRequestContext the container request context
     *
     * @return the instance of this object
     */
    public Jaxrs2ContextBuilder setContainerRequestContext(ContainerRequestContext containerRequestContext) {

        this.containerRequestContext = containerRequestContext;
        return this;
    }

    /**
     * Sets the container response context.
     *
     * @param containerResponseContext the container request context
     *
     * @return the instance of this object
     */
    public Jaxrs2ContextBuilder setContainerResponseContext(ContainerResponseContext containerResponseContext) {

        this.containerResponseContext = containerResponseContext;
        return this;
    }

    /**
     * Sets the request entity.
     *
     * @param requestEntity the request entity
     */
    public Jaxrs2ContextBuilder setRequestEntity(Object requestEntity) {

        this.requestEntity = requestEntity;
        return this;
    }

    /**
     * Sets the response entity.
     *
     * @param responseEntity the response entity
     */
    public Jaxrs2ContextBuilder setResponseEntity(Object responseEntity) {

        this.responseEntity = responseEntity;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build() {

        restContext.setHttpRequest(buildHttpRequest());
        restContext.setHttpResponse(buildHttpResponse());
        // TODO there is now way to access directly the Response object
        // restContext.setResponse();
        restContext.setSecurityContext(buildSecurityContext());
    }

    /**
     * Builds the {@link HttpRequest}.
     *
     * @return the {@link HttpRequest}
     */
    private HttpRequest buildHttpRequest() {

        HttpRequestImpl httpRequest = new HttpRequestImpl();

        if (containerRequestContext != null) {

            httpRequest.setMethod(getHttpMethod(containerRequestContext.getMethod()));
            httpRequest.setHeaders(new MultivaluedMapImpl<String, String>(containerRequestContext.getHeaders()));

            httpRequest.setContentType(getMediaTypeName(containerRequestContext.getMediaType()));
            // makes a shallow copy preventing from allowing the original map from being modified
            httpRequest.setEntity(requestEntity);
        }

        return httpRequest;
    }

    /**
     * Builds the {@link HttpResponse}.
     *
     * @return the {@link HttpResponse}
     */
    private HttpResponse buildHttpResponse() {

        HttpResponseImpl httpResponse = new HttpResponseImpl();

        if (containerResponseContext != null) {

            httpResponse.setStatusCode(containerResponseContext.getStatus());
            httpResponse.setHeaders(containerResponseContext.getHeaders());

            httpResponse.setContentType(getMediaTypeName(containerResponseContext.getMediaType()));
            httpResponse.setEntity(responseEntity);
        }

        return httpResponse;
    }

    /**
     * Builds the {@link SecurityContext}.
     *
     * @return the {@link SecurityContext}
     */
    private SecurityContext buildSecurityContext() {

        SecurityContextImpl securityContext = new SecurityContextImpl();

        if (containerRequestContext != null && containerRequestContext.getSecurityContext() != null) {
            securityContext.setAuthenticationScheme(containerRequestContext.getSecurityContext().getAuthenticationScheme());
            securityContext.setPrincipal(containerRequestContext.getSecurityContext().getUserPrincipal());
        }

        return securityContext;
    }

    /**
     * Retrieves the content type or null if it is not set.
     *
     * @param mediaType the media type
     *
     * @return the content type/mime name
     */
    private static String getMediaTypeName(MediaType mediaType) {

        return mediaType != null ? mediaType.toString() : null;
    }

    /**
     * Maps the http method name into corresponding {@link org.jboss.arquillian.extension.rest.warp.api.HttpMethod}.
     *
     * @param methodName the http method name
     *
     * @return the {@link org.jboss.arquillian.extension.rest.warp.api.HttpMethod}
     */
    private static HttpMethod getHttpMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }

    /**
     * Returns list of http headers values.
     *
     * @param values the list of values
     *
     * @return the list of values
     */
    private List<String> getHttpValueList(List<Object> values) {

        List<String> result = new ArrayList<String>();
        for (Object val : values) {
            result.add(val.toString());
        }
        return result;
    }

    /**
     * Retrieves the builder from the request.
     *
     * @param executionContext the execution context
     *
     * @return the {@link Jaxrs2ContextBuilder} instance
     */
    private static Jaxrs2ContextBuilder getJaxrsContextBuilder(ExecutionContext executionContext) {

        Jaxrs2ContextBuilder jaxrsContextBuilder = (Jaxrs2ContextBuilder)
                executionContext.getProperty(BUILDER_ATTRIBUTE_NAME);

        if (jaxrsContextBuilder == null) {

            jaxrsContextBuilder = new Jaxrs2ContextBuilder(executionContext);
            executionContext.setProperty(BUILDER_ATTRIBUTE_NAME, jaxrsContextBuilder);
        }

        return jaxrsContextBuilder;
    }

    /**
     * Retrieves the {@link org.jboss.arquillian.extension.rest.warp.api.RestContext} stored in the request.
     * <p/>
     * If non exists, then new one is being created.
     *
     * @return the rest context
     */
    private RestContextImpl getRestContext() {

        RestContextImpl restContext = (RestContextImpl)
                executionContext.getProperty(WarpRestCommons.WARP_REST_ATTRIBUTE);

        if (restContext == null) {

            restContext = new RestContextImpl();
            executionContext.setProperty(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
        }

        return restContext;
    }

    public static interface ExecutionContext {

        void setProperty(String name, Object value);

        Object getProperty(String name);
    }

    private static class RequestExecutionContext implements ExecutionContext {

        private final ContainerRequestContext containerRequestContext;

        private RequestExecutionContext(ContainerRequestContext containerRequestContext) {
            this.containerRequestContext = containerRequestContext;
        }

        @Override
        public void setProperty(String name, Object value) {

            containerRequestContext.setProperty(name, value);
        }

        @Override
        public Object getProperty(String name) {

            return containerRequestContext.getProperty(name);
        }
    }

    private static class InterceptionExecutionContext implements ExecutionContext {

        private final InterceptorContext interceptorContext;

        private InterceptionExecutionContext(InterceptorContext interceptorContext) {
            this.interceptorContext = interceptorContext;
        }

        @Override
        public void setProperty(String name, Object value) {

            interceptorContext.setProperty(name, value);
        }

        @Override
        public Object getProperty(String name) {

            return interceptorContext.getProperty(name);
        }
    }
}
