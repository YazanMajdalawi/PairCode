package com.code.pair.yazan.paircode.controller.stomp;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@AllArgsConstructor
public class WebSocketEventListener {
    private final ConcurrentHashMap<String, Set<String>> topicSubscriptions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToTopic = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public synchronized void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String topic = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();

        if (sessionId == null) return;
        if (topic == null) return;
        if (event.getUser() == null) return;

        String username = event.getUser().getName();

        sessionToTopic.put(sessionId, topic);
        if (!topicSubscriptions.containsKey(topic)) {
            topicSubscriptions.put(topic, new ConcurrentSkipListSet<>());
        }
        topicSubscriptions.get(topic).add(username);
    }

    @EventListener
    public synchronized void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String topic = sessionToTopic.get(sessionId);
        String username = event.getUser().getName();
        topicSubscriptions.get(topic).remove(username);
    }

    @EventListener
    public synchronized void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String topic = sessionToTopic.get(sessionId);
        String username = event.getUser().getName();
        topicSubscriptions.get(topic).remove(username);
    }

    public Set<String> getSubscribers(String topic) {
        return topicSubscriptions.getOrDefault(topic, new HashSet<>());
    }
}