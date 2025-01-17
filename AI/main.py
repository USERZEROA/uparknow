import asyncio
from UtilWebSocket import WebSocketClient
import random

async def event_pschanged():
    while True:
        await asyncio.sleep(random.randint(1, 3))
        event_happened = random.choice([True, False])
        if event_happened:
            print("Something should be sent.")
            yield True
        else:
            yield False


async def main_loop():
    WEBSOCKET_SERVER_URL = "wss://84b7-2601-681-4d00-2e30-202f-d912-4854-207b.ngrok-free.app/ws-camera"
    client = WebSocketClient(WEBSOCKET_SERVER_URL)

    await client.connect()

    if not client.websocket:
        print("Connect Fail.")
        return

    try:
        event_gen = event_pschanged()

        async for event in event_gen:
            if event: 
                message = "事件触发消息！"
                await client.send_message(message)

    except asyncio.CancelledError:
        print("Main loop is end.")
    finally:
        await client.close()


if __name__ == "__main__":
    asyncio.run(main_loop())