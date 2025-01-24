import asyncio
import websockets

# WebSocket server handler
async def echo(websocket):
    print("A client has connected.")
    try:
        async for message in websocket:
            print(f"Received message: {message}")
            response = "OK"
            await websocket.send(response)
            print(f"Sent response: {response}")
    except websockets.exceptions.ConnectionClosedError as e:
        print(f"Connection closed. ERR: {e}")
    except Exception as e:
        print(f"Error occurred: {e}")

# Start the WebSocket server
async def main():
    server_url = "localhost"
    server_port = 8765

    print(f"Starting WebSocket server on ws://{server_url}:{server_port}")
    async with websockets.serve(echo, server_url, server_port):
        await asyncio.Future()  # Run forever

if __name__ == "__main__":
    asyncio.run(main())
