package org.jboss.arquillian.extension.rest.client;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

import java.util.Map;

public class HeaderFilter implements ClientExecutionInterceptor {

    private final Map<String, String> headers;

    public HeaderFilter(Map<String, String> headers)
    {
        this.headers = headers;
    }

    @Override
    public ClientResponse execute(ClientExecutionContext ctx) throws Exception
    {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            ctx.getRequest().header(entry.getKey(), entry.getValue());
        }
        return ctx.proceed();
    }
}
