import asyncio
from UtilWebSocket import WebSocketClient
import random

async def event_pschanged():
    while True:
        await asyncio.sleep(random.randint(1, 3))
        event_happened = random.choice([True, False])
        if event_happened:
            print("event_happened")
            yield True
        else:
            yield False


async def main_loop():
    WEBSOCKET_SERVER_URL = "wss://0cc5-2601-681-4d00-2e30-18e0-653b-7419-d644.ngrok-free.app/ws-camera"
    client = WebSocketClient(WEBSOCKET_SERVER_URL)

    await client.connect()

    if not client.websocket:
        print("Connect Fail.")
        return

    try:
        event_gen = event_pschanged()

        async for event in event_gen:
            if event: 
                messages = [
                    """{ "role": 1, "parkingLot": 1, "parkingSpacePosition": [1, 1], "availability": 1 }""",
                    """{ "role": 1, "parkingLot": 2, "parkingSpacePosition": [1, 2], "availability": 1 }""",
                    """{ "role": 1, "parkingLot": 2, "parkingSpacePosition": [1, 1], "availability": 0 }""",
                    """{ "role": 1, "parkingLot": 2, "parkingSpacePosition": [1, 1], "availability": 1 }"""
                ]

                selected_message = random.choice(messages)
                await client.send_message(selected_message)

    except asyncio.CancelledError:
        print("Main loop is end.")
    finally:
        await client.close()


if __name__ == "__main__":
    asyncio.run(main_loop())