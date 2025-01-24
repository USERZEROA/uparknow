import asyncio
import json
from UtilWebSocket import WebSocketClient
from ParkingLotSurveillance import ParkingLotMonitor
import time


async def monitor_parking_lot(monitor, websocket_client, start_time, duration):
    """
    Monitor the parking lot and send updates via WebSocket.
    Ensures that one frame is processed every second, regardless of the processing time.
    """
    current_time = start_time
    end_time = start_time + duration

    while current_time < end_time:
        start = time.time() 
        
        messages = monitor.process_frame(current_time)
        for message in messages:
            await websocket_client.send_message(json.dumps(message))

        current_time += 1
        elapsed = time.time() - start
        sleep_time = max(0, 1 - elapsed) 
        if sleep_time == 0: print("WARNIGN: Taking too many time(>1sec) for processing 1 frame. Timeout handling.")
        await asyncio.sleep(sleep_time)


async def main():
    #websocket_url = "ws://localhost:8765"
    websocket_url = "wss://9764-2601-681-4d00-2e30-e8dd-528d-8a00-ee18.ngrok-free.app/ws-camera"
    websocket_client = WebSocketClient(websocket_url)

    await websocket_client.connect()
    if not websocket_client.websocket:
        print("WebSocket connection failed.")
        return

    monitor1 = ParkingLotMonitor(
        video_path="Parkng_Lot_Surveillance_Video.mp4",
        json_path="flipped_car_coordinates_with_parking_positions.json",
        lot_number=1,
        initial_time=280
    )

    try:
        await monitor_parking_lot(monitor1, websocket_client, start_time=280, duration=20)
    finally:
        monitor1.close()
        await websocket_client.close()


if __name__ == "__main__":
    asyncio.run(main())
