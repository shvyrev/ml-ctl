package io.cx.ml.cli.services;

import io.cx.ml.cli.clients.SessionClient;
import io.cx.ml.cli.dto.ListResponse;
import io.cx.ml.cli.dto.UploadSessionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class TestService {

    @Inject
    @RestClient
    SessionClient sessionClient;

    public void test() {
        log.info("TestService.test()");

        System.out.println("Hello, world!");

        ListResponse<UploadSessionResponse> sessions = sessionClient.getSessions(0, 100);
        sessions.getData().forEach(s ->
                log.info("Session: {}", s));
    }
}
