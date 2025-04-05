package edu.utah.cs.uparknow.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class FrontEndWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(FrontEndWebSocketHandler.class);
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

    @SuppressWarnings("null")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        //System.out.println("New FrontEnd connection: " + session.getId());

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
            logger.error("Error sending all parking spaces to session {}", session.getId(), e);
        }
    }

    private void sendAllLocationsTo(WebSocketSession session) {
        try {
            List<Locations> allLocations = locationsRepository.findAll();
            String json = objectMapper.writeValueAsString(allLocations);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.error("Error sending all locations to session " + session.getId(), e);
        }
    }

    private void sendAllParkingLotBoundsTo(WebSocketSession session) {
        try {
            List<ParkingLotBounds> allBounds = parkingLotBoundsRepository.findAll();
            String json = objectMapper.writeValueAsString(allBounds);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.error("Error sending all parking lot bounds to session " + session.getId(), e);
        }
    }

    @SuppressWarnings("null")
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        //System.out.println("FrontEnd connection closed: " + session.getId());
    }

    @SuppressWarnings("null")
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // System.out.println("Message from FrontEnd: " + payload);

        FrontEndSpacesDTO dto = objectMapper.readValue(payload, FrontEndSpacesDTO.class);
        Optional<ParkingSpaces> optionalPs = parkingSpacesRepository
                .findByLotIdAndSpaceRowAndSpaceColumn(
                        dto.getLotId(),
                        dto.getSpaceRow(),
                        dto.getSpaceColumn());

        ParkingSpaces ps;
        if (optionalPs.isPresent()) {
            ps = optionalPs.get();
        } else {
            ps = new ParkingSpaces();
            ps.setPermit_ID(dto.getPermitId());
            ps.setLot_ID(dto.getLotId());
            ps.setSpace_Row(dto.getSpaceRow());
            ps.setSpace_Column(dto.getSpaceColumn());
        }

        ps.setSpaceDisabled(dto.getSpaceDisabled());
        ps.setSpace_Lon(dto.getSpaceLon());
        ps.setSpace_Lat(dto.getSpaceLat());
        ps.setSpace_Parked(dto.getSpaceParked());

        parkingSpacesRepository.save(ps);
        broadcastUpdatedSpace(dto.getLotId(), dto.getSpaceRow(), dto.getSpaceColumn());
    }

    public void broadcastUpdatedSpace(Integer lotId, Integer row, Integer column) {
        Optional<ParkingSpaces> optionalPs =
                parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(lotId, row, column);

        if (optionalPs.isPresent()) {
            ParkingSpaces ps = optionalPs.get();

            try {
                String json = objectMapper.writeValueAsString(ps);
                for (WebSocketSession s : sessions) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(json));
                    }
                }
            } catch (IOException e) {
                logger.error("Error broadcasting updated space for lot {}, row {}, column {}",
                        lotId, row, column, e);
            }
        } else {
            logger.warn("No parking space found for lot={}, row={}, col={}", lotId, row, column);
        }
    }

    public void sendAllParkingSpacesToAllClients() {
        try {
            List<ParkingSpaces> allSpaces = parkingSpacesRepository.findAll();
            String json = objectMapper.writeValueAsString(allSpaces);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (IOException e) {
            logger.error("Error broadcasting all parking spaces to frontend clients", e);
        }
    }
}
