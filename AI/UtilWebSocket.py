import asyncio
import websockets

WEBSOCKET_SERVER_URL = "wss://84b7-2601-681-4d00-2e30-202f-d912-4854-207b.ngrok-free.app/ws-camera"

async def websocket_client():
    try:
        async with websockets.connect(WEBSOCKET_SERVER_URL) as websocket:
            print(f"Connected to {WEBSOCKET_SERVER_URL}")

            message = "Hello from Python WebSocket client!"
            print(f"Sending: {message}")
            await websocket.send(message)

            response = await websocket.recv()
            print(f"Received: {response}")

    except websockets.exceptions.ConnectionClosedError as e:
        print(f"WebSocket connection closed with error: {e}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    asyncio.run(websocket_client())
