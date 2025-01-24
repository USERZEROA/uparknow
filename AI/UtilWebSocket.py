import websockets

class WebSocketClient:
    def __init__(self, server_url):
        self.server_url = server_url
        self.websocket = None

    async def connect(self):
        try:
            self.websocket = await websockets.connect(self.server_url)
            print(f"Connected to {self.server_url}")
        except Exception as e:
            print(f"unable to connect {self.server_url}：{e}")
            self.websocket = None

    async def send_message(self, message):
        if self.websocket:
            try:
                print(f"send message:{message}")
                await self.websocket.send(message)
                response = await self.websocket.recv()
                if response != "OK":
                    print(f"UNEXPECTED server response：{response}")
                return response
            except websockets.exceptions.ConnectionClosedError as e:
                print(f"WebSocket closed. ERR: {e}")
            except Exception as e:
                print(f"Err during sending or geting:{e}")

    async def close(self):
        if self.websocket:
            try:
                await self.websocket.close()
                print("WebSocket is closed")
            except Exception as e:
                print(f"Error：{e}")