package edu.utah.cs.uparknow.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utah.cs.uparknow.model.Managers;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ClosuresRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import edu.utah.cs.uparknow.service.ManagerDataService;
import edu.utah.cs.uparknow.service.ManagersService;
import edu.utah.cs.uparknow.util.JwtUtil;

public class ManagerWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagerWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ManagersService managersService;
    private final ParkingSpacesRepository parkingSpacesRepository;
    private final FrontEndWebSocketHandler frontEndWebSocketHandler;

    public ManagerWebSocketHandler(ManagersService managersService,
                                   ParkingSpacesRepository parkingSpacesRepository,
                                    ClosuresRepository closuresRepository,
                                    FrontEndWebSocketHandler frontEndWebSocketHandler) {
        this.managersService = managersService;
        this.parkingSpacesRepository = parkingSpacesRepository;
        this.frontEndWebSocketHandler = frontEndWebSocketHandler;
    }

    @Override
    protected void handleTextMessage(@SuppressWarnings("null") WebSocketSession session, @SuppressWarnings("null") TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("ManagerWebSocket Received: {}", payload);

        //System.out.println("ManagerWebSocket Received: " + payload);

        JsonNode jsonNode = objectMapper.readTree(payload);
        if (jsonNode == null || !jsonNode.has("type")) {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"No type specified\"}"));
            return;
        }

        String type = jsonNode.get("type").asText();
        switch (type) {
            case "login" -> handleLogin(session, jsonNode);

            case "reset_password" -> handleResetPassword(session, jsonNode);

            case "manager_data" -> handleManagerData(session, jsonNode);

            default -> session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"Unknown request type\"}"));
        }
    }

    private void handleLogin(WebSocketSession session, JsonNode jsonNode) throws IOException {
        // {
        //   "type": "login",
        //   "payload": {
        //     "username": "...",
        //     "password": "..."
        //   }
        // }
        JsonNode payloadNode = jsonNode.get("payload");
        if (payloadNode == null 
            || !payloadNode.has("username") 
            || !payloadNode.has("password")) {
            session.sendMessage(new TextMessage("{\"type\":\"login_failed\",\"message\":\"Invalid payload\"}"));
            return;
        }

        String username = payloadNode.get("username").asText();
        String rawPassword = payloadNode.get("password").asText();

        Optional<Managers> managerOpt = managersService.findByUsername(username);
        if (!managerOpt.isPresent()) {
            session.sendMessage(new TextMessage("{\"type\":\"login_failed\",\"message\":\"Invalid username or password\"}"));
            return;
        }

        Managers manager = managerOpt.get();
        if (!managersService.checkPassword(rawPassword, manager.getManaPassword())) {
            session.sendMessage(new TextMessage("{\"type\":\"login_failed\",\"message\":\"Invalid username or password\"}"));
            return;
        }

        String token = managersService.generateToken(manager);

        // {
        //   "type": "login_success",
        //   "Mana_ID": ...
        //   "token": "......"
        // }
        String jsonResp = String.format("{\"type\":\"login_success\",\"Mana_ID\":%d,\"token\":\"%s\"}", manager.getManaId(), token);
        session.sendMessage(new TextMessage(jsonResp));
    }

    private void handleResetPassword(WebSocketSession session, JsonNode jsonNode) throws IOException {
        // {
        //   "type": "reset_password",
        //   "payload": {
        //     "username": "user@example.com"
        //   }
        // }
        JsonNode payloadNode = jsonNode.get("payload");
        if (payloadNode == null || !payloadNode.has("username")) {
            session.sendMessage(new TextMessage("{\"type\":\"reset_failed\",\"message\":\"Invalid payload\"}"));
            return;
        }

        String username = payloadNode.get("username").asText();
        boolean success = managersService.resetPasswordFor(username);
        if (success) {
            session.sendMessage(
                    new TextMessage("{\"type\":\"reset_success\",\"message\":\"Reset link sent to your email\"}"));
        } else {
            session.sendMessage(new TextMessage("{\"type\":\"reset_failed\",\"message\":\"Email not found\"}"));
        }
    }
    
    @Autowired
    private ManagerDataService managerDataService;
    public void handleManagerData(WebSocketSession session, JsonNode jsonNode) throws IOException {
        if (!jsonNode.has("token")) {
            unauthorized(session);
            return;
        }

        String token = jsonNode.get("token").asText();
        boolean valid = managersService.validateToken(token);
        if (!valid) {
            unauthorized(session);
            return;
        }

        int currentManaId = getCurrentManagerId(token);
        if (!jsonNode.has("payload") || jsonNode.get("payload").isNull()) {
            List<ParkingSpaces> allSpaces = parkingSpacesRepository.findAll();
            for (ParkingSpaces ps : allSpaces) {
                if (ps.getClosures() != null) {
                    ps.setClosures(
                        ps.getClosures().stream()
                            .filter(closure -> closure.getManaId() == currentManaId)
                            .collect(Collectors.toList())
                    );
                }
            }          
            String allSpacesJson = objectMapper.writeValueAsString(allSpaces);
            String resp = String.format("{\"type\":\"manager_data\",\"payload\":%s}", allSpacesJson);
            
            session.sendMessage(new TextMessage(resp));
        } else {
            JsonNode payloadNode = jsonNode.get("payload");
            if (!payloadNode.isArray()) {
                session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"Payload must be array\"}"));
                return;
            }
            managerDataService.updateManagerClosures(payloadNode, currentManaId);
            session.sendMessage(new TextMessage("{\"type\":\"manager_data_updated\",\"message\":\"ParkingSpaces updated\"}"));

            frontEndWebSocketHandler.sendAllParkingSpacesToAllClients();
        }
    }

    private int getCurrentManagerId(String token) {
        Managers manager = managersService.findByUsername(JwtUtil.getUsernameFromToken(token))
                                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return manager.getManaId();
    }

    private void unauthorized(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("{\"type\":\"unauthorized\",\"message\":\"Token expired or invalid\"}"));
    }
}
