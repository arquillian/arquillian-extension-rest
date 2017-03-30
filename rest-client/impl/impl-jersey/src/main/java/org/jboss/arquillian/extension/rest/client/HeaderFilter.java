package org.jboss.arquillian.extension.rest.client;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.util.Map;

public class HeaderFilter implements ClientRequestFilter {

    private final Map<String, String> headers;

    public HeaderFilter(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            clientRequestContext.getHeaders().add(entry.getKey(), entry.getValue());
        }
    }
}
