package com.karandashov.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class SseController {

    private static final Set<SseEmitter> CLIENTS = ConcurrentHashMap.newKeySet();

    @GetMapping("/news/listen")
    public SseEmitter listen() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        CLIENTS.add(emitter);

        emitter.onCompletion(() -> CLIENTS.remove(emitter));
        emitter.onTimeout(() -> CLIENTS.remove(emitter));
        emitter.onError((e) -> CLIENTS.remove(emitter));

        return emitter;
    }

    public void broadcast(Object data) {
        for (SseEmitter emitter : CLIENTS) {
            try {
                emitter.send(data);
            } catch (IOException e) {
                log.error("Error sending SSE to client", e);
                emitter.completeWithError(e);
            }
        }
    }
}
