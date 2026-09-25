package com.karandashov;

import io.jooby.test.JoobyTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(JoobyApp.class)
public class JoobyAppTest {

    private static final HttpClient client = HttpClient.newHttpClient();

    @Test
    public void testGetAll(io.jooby.Jooby app) throws IOException, InterruptedException {
        int serverPort = app.getServerOptions().getPort();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + serverPort + "/news"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetNotFound(io.jooby.Jooby app) throws IOException, InterruptedException {
        int serverPort = app.getServerOptions().getPort();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + serverPort + "/news/00000000-0000-0000-0000-000000000000"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreate(io.jooby.Jooby app) throws IOException, InterruptedException {
        int serverPort = app.getServerOptions().getPort();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + serverPort + "/news"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"Test Title\", \"message\":\"Test Message\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
