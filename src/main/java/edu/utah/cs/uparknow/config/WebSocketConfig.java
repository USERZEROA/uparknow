package edu.utah.cs.uparknow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import edu.utah.cs.uparknow.repository.LocationsRepository;
import edu.utah.cs.uparknow.repository.ParkingLotBoundsRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ParkingSpacesRepository parkingSpacesRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private ParkingLotBoundsRepository parkingLotBoundsRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myCameraHandler(), "/ws-camera")
                .setAllowedOriginPatterns("*");

        registry.addHandler(frontEndHandler(), "/ws-frontend")
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public WebSocketHandler myCameraHandler() {
        return new CameraWebSocketHandler(parkingSpacesRepository, (FrontEndWebSocketHandler) frontEndHandler());
    }

    @Bean
    public WebSocketHandler frontEndHandler() {
        return new FrontEndWebSocketHandler(parkingSpacesRepository, locationsRepository, parkingLotBoundsRepository);
    }
}
