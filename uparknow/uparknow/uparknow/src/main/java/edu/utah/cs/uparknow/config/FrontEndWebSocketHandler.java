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

import edu.utah.cs.uparknow.model.FrontEndSpacesDTO;
import edu.utah.cs.uparknow.model.Locations;
import edu.utah.cs.uparknow.model.ParkingLotBounds;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.LocationsRepository;
import edu.utah.cs.uparknow.repository.ParkingLotBoundsRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

// 用于与“前端”通信的 WebSocket Handler
public class FrontEndWebSocketHandler extends TextWebSocketHandler {

    private final ParkingSpacesRepository parkingSpacesRepository;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final LocationsRepository locationsRepository;
    private final ParkingLotBoundsRepository parkingLotBoundsRepository;

    public FrontEndWebSocketHandler(ParkingSpacesRepository parkingSpacesRepository, LocationsRepository locationsRepository, ParkingLotBoundsRepository parkingLotBoundsRepository) {
    this.parkingSpacesRepository = parkingSpacesRepository;
    this.locationsRepository = locationsRepository;
    this.parkingLotBoundsRepository = parkingLotBoundsRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New FrontEnd connection: " + session.getId());

        // 先发送停车位信息，再发送位置信息
        sendAllParkingSpacesTo(session);
        sendAllLocationsTo(session);
        sendAllParkingLotBoundsTo(session);
    }
    
    private void sendAllParkingSpacesTo(WebSocketSession session) {
        try {
            List<ParkingSpaces> allSpaces = parkingSpacesRepository.findAll();
            String json = objectMapper.writeValueAsString(allSpaces);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAllLocationsTo(WebSocketSession session) {
        try {
            List<Locations> allLocations = locationsRepository.findAll();
            String json = objectMapper.writeValueAsString(allLocations);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAllParkingLotBoundsTo(WebSocketSession session) {
        try {
            List<ParkingLotBounds> allBounds = parkingLotBoundsRepository.findAll();
            String json = objectMapper.writeValueAsString(allBounds);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("FrontEnd connection closed: " + session.getId());
    }

    // 处理前端消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Message from FrontEnd: " + payload);

        // 1 解析成 DTO
        FrontEndSpacesDTO dto = objectMapper.readValue(payload, FrontEndSpacesDTO.class);

        // 2 根据 lotId, spaceRow, spaceColumn 查数据库
        Optional<ParkingSpaces> optionalPs = parkingSpacesRepository
                .findByLotIdAndSpaceRowAndSpaceColumn(
                        dto.getLotId(),
                        dto.getSpaceRow(),
                        dto.getSpaceColumn());

        ParkingSpaces ps;
        if (optionalPs.isPresent()) {
            // 更新
            ps = optionalPs.get();
            System.out.println("Found existing record, will update it");
        } else {
            // 新建
            ps = new ParkingSpaces();

            // 使用前端传来的 Permit_ID
            ps.setPermit_ID(dto.getPermitId());

            ps.setLot_ID(dto.getLotId());
            ps.setSpace_Row(dto.getSpaceRow());
            ps.setSpace_Column(dto.getSpaceColumn());
        }

        // 新增赋值
        ps.setSpaceDisabled(dto.getSpaceDisabled());
        ps.setSpace_Lon(dto.getSpaceLon());
        ps.setSpace_Lat(dto.getSpaceLat());

        // availability: 0 -> 车位被占 (true)；1 -> 车位空置 (false)
        ps.setSpace_Parked(dto.getSpaceParked());

        // 4 保存
        parkingSpacesRepository.save(ps);

        // 5 广播给所有前端
        broadcastUpdatedSpace(dto.getLotId(), dto.getSpaceRow(), dto.getSpaceColumn());
    }

    // 向所有前端广播某个停车位更新后的信息
    public void broadcastUpdatedSpace(Integer lotId, Integer row, Integer column) {
        // 1. 查询数据库获取最新的数据
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
