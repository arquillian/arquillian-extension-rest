package org.jboss.arquillian.extension.rest.client;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.jboss.arquillian.test.spi.TestEnricher;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RestEnricher extends BaseRestEnricher implements TestEnricher {

    @Override
    protected boolean isSupportedParameter(Class<?> clazz)
    {
        return true; // it's proxy based, exception will be thrown when proxying.
    }

    @Override
    protected Object enrichByType(Class<?> clazz, Method method, ArquillianResteasyResource annotation, Consumes consumes, Produces produces)
    {
        Object value;
        Client client = JerseyClientBuilder.newClient();
        WebTarget webTarget = client.target(getBaseURL() + ((ArquillianResteasyResource) annotation).value());
        JerseyWebTarget jerseyWebTarget = (JerseyWebTarget) webTarget;
        if (JerseyWebTarget.class.isAssignableFrom(clazz)) {
            value = jerseyWebTarget;
        } else {
            final Class<?> parameterType;
            try {
                final Annotation[] methodDeclaredAnnotations = method.getDeclaredAnnotations();
//                                This is test method so if it only contains @Test annotation then we don't need to hassel with substitutions
                parameterType = methodDeclaredAnnotations.length <= 1 ? clazz : ClassModifier.getModifiedClass(clazz, methodDeclaredAnnotations);
            } catch (Exception e) {
                throw new RuntimeException("Cannot substitute annotations for method " + method.getName(), e);
            }
            value = WebResourceFactory.newResource(parameterType, jerseyWebTarget);
        }
        return value;
    }
}