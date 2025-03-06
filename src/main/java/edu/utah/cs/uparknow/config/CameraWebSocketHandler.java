package edu.utah.cs.uparknow.config;

import java.util.Optional;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utah.cs.uparknow.model.CameraDataDTO;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

public class CameraWebSocketHandler extends TextWebSocketHandler {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ParkingSpacesRepository parkingSpacesRepository;

    // 用来给前端发送更新的 handler
    private FrontEndWebSocketHandler frontEndHandler;

    public CameraWebSocketHandler(ParkingSpacesRepository parkingSpacesRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
    }

    // 构造方法，可同时注入 frontEndHandler
    public CameraWebSocketHandler(
            ParkingSpacesRepository parkingSpacesRepository,
            FrontEndWebSocketHandler frontEndHandler
    ) {
        this.parkingSpacesRepository = parkingSpacesRepository;
        this.frontEndHandler = frontEndHandler;
    }

    @SuppressWarnings("null")
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 先给摄像头回一个 "OK"
        session.sendMessage(new TextMessage("OK"));

        String jsonPayload = message.getPayload();
        // System.out.println("Received message: " + jsonPayload);

        // 1. 解析 JSON 为 DTO
        CameraDataDTO dto = objectMapper.readValue(jsonPayload, CameraDataDTO.class);

        if (parkingSpacesRepository != null) {
            Integer lotId = dto.getParkingLot();
            Integer row = dto.getParkingSpacePosition().get(0);
            Integer column = dto.getParkingSpacePosition().get(1);
            Integer availability = dto.getAvailability();

            // 2. 查找数据库中是否已有对应记录
            Optional<ParkingSpaces> optionalPs =
                    parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(lotId, row, column);

            ParkingSpaces ps;
            if (optionalPs.isPresent()) {
                // 3a. 已有记录更新
                ps = optionalPs.get();
            } else {
                // 3b. 无记录新建
                ps = new ParkingSpaces();

                // Permit_ID 暂时为 1
                ps.setPermit_ID(1);

                ps.setLot_ID(lotId);
                ps.setSpace_Row(row);
                ps.setSpace_Column(column);
            }

            // availability: 0 -> 车位被占 (true)；1 -> 车位空置 (false)
            ps.setSpace_Parked(availability == 0);

            // 保存到数据库
            parkingSpacesRepository.save(ps);

            // 4. 通知前端更新
            if (frontEndHandler != null) {
                frontEndHandler.broadcastUpdatedSpace(lotId, row, column);
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接成功
        // System.out.println("New WebSocket connection: " + session.getId());
    }

    @SuppressWarnings("null")
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭
        // System.out.println("WebSocket closed: " + session.getId());
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ParkingSpacesRepository getParkingSpacesRepository() {
        return parkingSpacesRepository;
    }

    public void setParkingSpacesRepository(ParkingSpacesRepository parkingSpacesRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
    }
}
