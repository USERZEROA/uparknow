package edu.utah.cs.uparknow;

import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 测试 CameraWebSocketHandler 端点： /ws-camera
 * 启动随机端口, 使用 Java-WebSocket 客户端模拟连接.
 * 并使用 @MockBean ParkingSpacesRepository 不会改动真实数据库
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CameraWebSocketHandlerTest {

    @LocalServerPort
    private int port;

    // 用 @MockBean 模拟 ParkingSpacesRepository, 避免真实数据库操作
    @MockBean
    private ParkingSpacesRepository parkingSpacesRepository;

    @BeforeEach
    void setUpMock() {
        // 假设数据库里已经存在一个 Lot_ID=1, Row=1, Column=1 的车位
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(123);
        existing.setLot_ID(1);
        existing.setSpace_Row(1);
        existing.setSpace_Column(1);
        existing.setSpace_Parked(false);

        // availability=0 => parked=true
        // availability=1 => parked=false
        Mockito.when(parkingSpacesRepository.findByLotIdAndSpaceRowAndSpaceColumn(1, 1, 1))
               .thenReturn(Optional.of(existing));

        // save(...) 返回传入的对象本身
        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
               .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testCameraWebSocketHandleTextMessage_availability0() throws Exception {
        // availability=0 => 车位被占
        String wsUrl = "ws://localhost:" + port + "/ws-camera";
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                // 连接成功后，发送 JSON
                String json = "{\"role\":1,\"parkingLot\":1,\"parkingSpacePosition\":[1,1],\"availability\":0}";
                send(json);
            }

            @Override
            public void onMessage(String message) {
                // 预期先收到一个 "OK"
                if ("OK".equals(message)) {
                    latch.countDown();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) { }
            @Override
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
        // availability=1 => 车位空置
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
        // availability=0 => 车位被占, 但找不到对应车位记录
        // 先 mock: findByLotIdAndSpaceRowAndSpaceColumn(2,2,2) => empty
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
