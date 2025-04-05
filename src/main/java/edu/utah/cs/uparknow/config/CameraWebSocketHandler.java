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
    private FrontEndWebSocketHandler frontEndHandler;

    public CameraWebSocketHandler(ParkingSpacesRepository parkingSpacesRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
    }

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
        session.sendMessage(new TextMessage("OK"));
        String jsonPayload = message.getPayload();

        // System.out.println("Received message: " + jsonPayload);

        CameraDataDTO dto = objectMapper.readValue(jsonPayload, CameraDataDTO.class);
        if (parkingSpacesRepository != null) {
            Integer lotId = dto.getParkingLot();
            Integer row = dto.getParkingSpacePosition().get(0);
            Integer column = dto.getParkingSpacePosition().get(1);
            Integer availability = dto.getAvailability();

            Optional<ParkingSpaces> optionalPs =
                    parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(lotId, row, column);

            ParkingSpaces ps;
            if (optionalPs.isPresent()) {
                ps = optionalPs.get();
            } else {
                ps = new ParkingSpaces();
                ps.setPermit_ID(1);
                ps.setLot_ID(lotId);
                ps.setSpace_Row(row);
                ps.setSpace_Column(column);
            }
            ps.setSpace_Parked(availability == 0);
            parkingSpacesRepository.save(ps);

            if (frontEndHandler != null) {
                frontEndHandler.broadcastUpdatedSpace(lotId, row, column);
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // System.out.println("New WebSocket connection: " + session.getId());
    }

    @SuppressWarnings("null")
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
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
