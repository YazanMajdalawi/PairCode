package com.code.pair.yazan.paircode.controller.stomp;

import com.code.pair.yazan.paircode.domain.ActiveProject;
import com.code.pair.yazan.paircode.service.InMemoryProjectsService;
import com.code.pair.yazan.paircode.dsa.TextModification;
import com.code.pair.yazan.paircode.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class ActiveProjectWsController {
    private final JwtService jwtService;
    private final InMemoryProjectsService projectsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketEventListener webSocketEventListener;

    @MessageMapping("/users/{projectId}/{filepath}/{username}")
    public void inquireActiveUsers(@DestinationVariable String projectId,
                                   @DestinationVariable String filepath,
                                   @DestinationVariable String username) {
        String path = String.format("/topic/users/%s/%s/%s", projectId, filepath, username);
        String topic = String.format("/topic/modify/%s/%s", projectId, filepath);
        messagingTemplate.convertAndSend(path, webSocketEventListener.getSubscribers(topic));
    }

    @MessageMapping("/read/{projectId}/{filepath}/{username}")
    public void readFile(@DestinationVariable String projectId,
                          @DestinationVariable String filepath,
                          @DestinationVariable String username) {

        var res = projectsService
                .getProject(URLDecoder.decode(projectId, StandardCharsets.UTF_8))
                .getFileContent(URLDecoder.decode(filepath, StandardCharsets.UTF_8));

        String path = String.format("/topic/read/%s/%s/%s", projectId, filepath, username);

        messagingTemplate.convertAndSend(path, res);
    }

    @MessageMapping("/modify/{projectId}/{filepath}")
    public void modify(
            @Payload List<TextModification> modification,
            @DestinationVariable String projectId,
            @DestinationVariable String filepath, Principal principal
    ) throws URISyntaxException {
        var proj = projectsService.getProject(projectId);
        String path = String.format("/topic/modify/%s/%s", projectId, filepath);

        for (TextModification mod : modification) {
            mod.setModifier(principal.getName());
            mod.setProjectId(URLDecoder.decode(projectId, StandardCharsets.UTF_8));
            mod.setFilePath(URLDecoder.decode(filepath, StandardCharsets.UTF_8));
        }

        messagingTemplate.convertAndSend(path, proj.applyModification(modification));
    }

    @MessageMapping("/save/{projectId}")
    public void save(@DestinationVariable String projectId, Principal principal) {
        String encodedUsername = URLEncoder.encode(principal.getName(), StandardCharsets.UTF_8);
        String path = String.format("/topic/save/%s/%s", projectId, encodedUsername);
        projectsService.saveProject(URLDecoder.decode(projectId, StandardCharsets.UTF_8));
        messagingTemplate.convertAndSend(path, true);
    }
}
