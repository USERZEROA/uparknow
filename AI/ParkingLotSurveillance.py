import json
import time
import torch
from transformers import DetrForObjectDetection, DetrImageProcessor
import cv2
from PIL import Image

class ParkingLotMonitor:
    def __init__(self, video_path, json_path, lot_number, initial_time):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print(f"Using device: {self.device} for ParkinLot {lot_number}")

        # Load model and processor
        self.model = DetrForObjectDetection.from_pretrained('facebook/detr-resnet-50').to(self.device)
        self.processor = DetrImageProcessor.from_pretrained('facebook/detr-resnet-50')
        self.model.eval()

        self.cap = cv2.VideoCapture(video_path)
        self.fps = self.cap.get(cv2.CAP_PROP_FPS)
        self.total_frames = int(self.cap.get(cv2.CAP_PROP_FRAME_COUNT))
        self.total_seconds = int(self.total_frames / self.fps)
        self.initial_time = initial_time
        self.lot_number = lot_number

        self.initial_frame_number = int(self.initial_time * self.fps)
        self.cap.set(cv2.CAP_PROP_POS_FRAMES, self.initial_frame_number)

        # Load parking box coordinates
        with open(json_path, 'r') as file:
            self.parking_boxes = json.load(file)
        
        ret, frame = self.cap.read()
        if not ret:
            raise ValueError(f"Cannot read the initial frame at {initial_time} seconds.")
        
        self.image_height = frame.shape[0]
        for box in self.parking_boxes:
            box["y1"] = self.image_height - box["y1"]
            box["y2"] = self.image_height - box["y2"]

    def calculate_iou(self, boxA, boxB):
        """Calculate Intersection over Union (IoU) between two boxes."""
        xA = max(boxA[0], boxB[0])
        yA = max(boxA[1], boxB[1])
        xB = min(boxA[2], boxB[2])
        yB = min(boxA[3], boxB[3])

        interWidth = max(0, xB - xA)
        interHeight = max(0, yB - yA)
        interArea = interWidth * interHeight

        boxAArea = (boxA[2] - boxA[0]) * (boxA[3] - boxA[1])
        boxBArea = (boxB[2] - boxB[0]) * (boxB[3] - boxB[1])
        iou = interArea / float(boxAArea + boxBArea - interArea)
        return iou

    def process_frame(self, time_in_seconds, show_frame=False):
        """Process a frame at a given time and return parking information."""
        messages = []
        frame_number = int(self.fps * time_in_seconds)
        self.cap.set(cv2.CAP_PROP_POS_FRAMES, frame_number)
        ret, frame = self.cap.read()

        if not ret or frame is None:
            print(f"Error: Cannot read frame at {time_in_seconds} seconds.")
            return

        image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
        inputs = self.processor(images=image, return_tensors="pt").to(self.device)

        with torch.no_grad():
            outputs = self.model(**inputs)

        logits = outputs.logits[0].cpu()
        boxes = outputs.pred_boxes[0].cpu()

        probas = logits.softmax(-1)
        keep = probas.max(-1).values > 0.5

        detected_boxes = []
        for box, cls in zip(boxes[keep], probas[keep]):
            category_id = cls.argmax().item()
            if category_id == 3:  # 3 is car
                x_center, y_center, width, height = box.numpy()
                x1 = int((x_center - width / 2) * frame.shape[1])
                y1 = int((y_center - height / 2) * frame.shape[0])
                x2 = int((x_center + width / 2) * frame.shape[1])
                y2 = int((y_center + height / 2) * frame.shape[0])
                detected_boxes.append((x1, y1, x2, y2))

        parking_status = [
            {"row": row, "column": col, "parked": False}
            for row in range(1, 3)
            for col in range(1, 14)
        ]

        for detected_box in detected_boxes:
            for parking_box in self.parking_boxes:
                parking_box_coords = [parking_box["x1"], parking_box["y1"], parking_box["x2"], parking_box["y2"]]
                iou = self.calculate_iou(detected_box, parking_box_coords)
                if iou > 0.5:
                    row, column = parking_box["row"], parking_box["column"]
                    for status in parking_status:
                        if status["row"] == row and status["column"] == column:
                            status["parked"] = True
        
        if show_frame:
            # Draw bounding boxes on frame, FOR DEBUG ONLY
            for parking_box in self.parking_boxes:
                x1, y1, x2, y2 = int(parking_box["x1"]), int(parking_box["y1"]), int(parking_box["x2"]), int(parking_box["y2"])
                cv2.rectangle(frame, (x1, y1), (x2, y2), color=(255, 0, 0), thickness=2)
                cv2.putText(frame, f'Row: {parking_box["row"]}, Col: {parking_box["column"]}', (x1, y1 - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0), 1)

            for detected_box in detected_boxes:
                x1, y1, x2, y2 = detected_box
                cv2.rectangle(frame, (x1, y1), (x2, y2), color=(0, 255, 0), thickness=2)
                cv2.putText(frame, 'Car', (x1, y1 - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 1)

            cv2.imshow(f"Frame at {time_in_seconds} seconds for ParkingLot {self.lot_number}", frame)
            cv2.waitKey(0)
            cv2.destroyAllWindows()

        print(f"\nTime:ParkingLot {self.lot_number} is now on {time_in_seconds} seconds")
        messages=[]
        for status in parking_status:
            row = status["row"]
            column = status["column"]
            availability = 1 if status["parked"] else 0
            message = {
                "role": 1,
                "parkingLot": self.lot_number,
                "parkingSpacePosition": [row, column],
                "availability": availability
            }
            messages.append(json.dumps(message))
        return messages


    def close(self):
        """Release video resources."""
        self.cap.release()


if __name__ == "__main__":

    monitor = ParkingLotMonitor(video_path = "Parkng_Lot_Surveillance_Video.mp4", json_path = "flipped_car_coordinates_with_parking_positions.json"
                                , lot_number=1, initial_time=280)

    for time_in_seconds in range(280, 280 + 30):
        print(monitor.process_frame(time_in_seconds, show_frame=False))
        time.sleep(1)

    monitor.close()
