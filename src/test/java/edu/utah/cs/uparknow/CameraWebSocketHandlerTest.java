package edu.utah.cs.uparknow;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CameraWebSocketHandlerTest {

    @LocalServerPort
    private int port;

    @SuppressWarnings("removal")
    @MockBean
    private ParkingSpacesRepository parkingSpacesRepository;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUpMock() {
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(123);
        existing.setLot_ID(1);
        existing.setSpace_Row(1);
        existing.setSpace_Column(1);
        existing.setSpace_Parked(false);

        Mockito.when(parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(1, 1, 1))
               .thenReturn(Optional.of(existing));

        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
               .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testCameraWebSocketHandleTextMessage_availability0() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws-camera";
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                String json = "{\"role\":1,\"parkingLot\":1,\"parkingSpacePosition\":[1,1],\"availability\":0}";
                send(json);
            }

            @Override
            public void onMessage(String message) {
                if ("OK".equals(message)) {
                    latch.countDown();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) { }

            @Override
            @SuppressWarnings("CallToPrintStackTrace")
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
        boolean receivedOk = latch.await(5, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertTrue(receivedOk, "Should receive 'OK' within 5 seconds");
        client.close();
    }

    @Test
    void testCameraWebSocketHandleTextMessage_availability1() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws-camera";
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                String json = "{\"role\":1,\"parkingLot\":1,\"parkingSpacePosition\":[1,1],\"availability\":1}";
                send(json);
            }

            @Override
            public void onMessage(String message) {
                if ("OK".equals(message)) {
                    latch.countDown();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) { }
            @Override
            @SuppressWarnings("CallToPrintStackTrace")
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
        boolean receivedOk = latch.await(5, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertTrue(receivedOk, "Should receive 'OK' within 5 seconds");
        client.close();
    }

    @Test
    void testCameraWebSocketHandleTextMessage_notFound() throws Exception {
        Mockito.when(parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(2, 2, 2))
               .thenReturn(Optional.empty());

        String wsUrl = "ws://localhost:" + port + "/ws-camera";
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                String json = "{\"role\":1,\"parkingLot\":2,\"parkingSpacePosition\":[2,2],\"availability\":0}";
                send(json);
            }

            @Override
            public void onMessage(String message) {
                if ("OK".equals(message)) {
                    latch.countDown();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) { }
            @Override
            @SuppressWarnings("CallToPrintStackTrace")
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
        boolean receivedOk = latch.await(5, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertTrue(receivedOk, "Should receive 'OK' within 5 seconds");
        client.close();
    }
}
