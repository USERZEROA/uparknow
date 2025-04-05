package edu.utah.cs.uparknow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import edu.utah.cs.uparknow.repository.ClosuresRepository;
import edu.utah.cs.uparknow.repository.LocationsRepository;
import edu.utah.cs.uparknow.repository.ParkingLotBoundsRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import edu.utah.cs.uparknow.service.ManagersService;
    
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private ParkingSpacesRepository parkingSpacesRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private ParkingLotBoundsRepository parkingLotBoundsRepository;

    @Autowired
    private ManagersService managersService;

    @Autowired
    private ClosuresRepository closuresRepository;

    @SuppressWarnings("null")
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myCameraHandler(), "/ws-camera")
                .setAllowedOriginPatterns("*");

        registry.addHandler(frontEndHandler(), "/ws-frontend")
                .setAllowedOriginPatterns("*");

        registry.addHandler(managerWebSocketHandler(), "/ws-manager")
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public WebSocketHandler myCameraHandler() {
        return new CameraWebSocketHandler(parkingSpacesRepository, (FrontEndWebSocketHandler) frontEndHandler());
    }

    @Bean
    public FrontEndWebSocketHandler frontEndHandler() {
        return new FrontEndWebSocketHandler(parkingSpacesRepository, locationsRepository, parkingLotBoundsRepository);
    }

    @Bean
    public WebSocketHandler managerWebSocketHandler() {
        return new ManagerWebSocketHandler(managersService, parkingSpacesRepository, closuresRepository, (FrontEndWebSocketHandler) frontEndHandler());
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(name = "websocket.container.enabled", havingValue = "true", matchIfMissing = true)
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(100 * 1024 * 1024);
        container.setMaxBinaryMessageBufferSize(100 * 1024 * 1024);
        return container;
    }
}
