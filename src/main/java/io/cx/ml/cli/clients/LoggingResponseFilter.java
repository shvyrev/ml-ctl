package io.cx.ml.cli.clients;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class LoggingResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        log.info("<< HTTP RESPONSE: {} {} -> Status: {} {}",
                requestContext.getMethod(),
                requestContext.getUri(),
                responseContext.getStatus(),
                responseContext.getStatusInfo().getReasonPhrase()
        );
    }
}