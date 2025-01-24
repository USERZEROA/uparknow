package edu.utah.cs.uparknow.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

// 用于与“前端”通信的 WebSocket Handler
public class FrontEndWebSocketHandler extends TextWebSocketHandler {

    private final ParkingSpacesRepository parkingSpacesRepository;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FrontEndWebSocketHandler(ParkingSpacesRepository parkingSpacesRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New FrontEnd connection: " + session.getId());

        // 一旦前端连接，就给它发送当前数据库的初始化数据
        sendAllSpacesTo(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("FrontEnd connection closed: " + session.getId());
    }

    // 如果前端会发消息给后端，可以在这里进行处理
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 这里仅示例打印接收到的消息
        System.out.println("Message from FrontEnd: " + message.getPayload());
    }

    // 给某个会话发送数据库中所有 ParkingSpaces 的信息，用于初始化
    private void sendAllSpacesTo(WebSocketSession session) {
        try {
            List<ParkingSpaces> allList = parkingSpacesRepository.findAll();
            String json = objectMapper.writeValueAsString(allList);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 向所有前端广播某个停车位更新后的信息
    public void broadcastUpdatedSpace(Integer lotId, Integer row, Integer column) {
        // 1. 查询数据库获取最新的空间数据
        Optional<ParkingSpaces> optionalPs =
                parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(lotId, row, column);

        if (optionalPs.isPresent()) {
            ParkingSpaces ps = optionalPs.get();

            try {
                // 2. 转为 JSON 字符串
                String json = objectMapper.writeValueAsString(ps);

                // 3. 向所有已连接的 WebSocketSession 发送更新消息
                for (WebSocketSession s : sessions) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(json));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No parking space found for lot=" + lotId
                    + ", row=" + row + ", col=" + column);
        }
    }
}
