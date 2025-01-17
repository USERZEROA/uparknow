import json
import torch
from transformers import DetrForObjectDetection, DetrImageProcessor
import cv2
from PIL import Image


class ParkingLotSurveillance:
    def __init__(self, video_path, model_name='facebook/detr-resnet-50', device=None):
        self.device = device or torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print(f"Using device: {self.device}")

        # Load model and processor
        self.model = DetrForObjectDetection.from_pretrained(model_name).to(self.device)
        self.processor = DetrImageProcessor.from_pretrained(model_name)
        self.model.eval()

        # Load video
        self.cap = cv2.VideoCapture(video_path)
        self.fps = self.cap.get(cv2.CAP_PROP_FPS)
        self.total_frames = int(self.cap.get(cv2.CAP_PROP_FRAME_COUNT))
        self.total_seconds = int(self.total_frames / self.fps)

    def set_parking_boxes(self, json_path):
        with open(json_path, 'r') as file:
            parking_boxes = json.load(file)

        # Flip coordinates for y-axis
        ret, frame = self.cap.read()
        if not ret:
            raise ValueError("Cannot read the initial frame to calculate flipped coordinates.")
        image_height = frame.shape[0]

        for box in parking_boxes:
            box["y1"] = image_height - box["y1"]
            box["y2"] = image_height - box["y2"]

        self.parking_boxes = parking_boxes

    def calculate_iou(self, boxA, boxB):
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

    def process_frame(self, time_in_seconds):
        frame_number = int(self.fps * time_in_seconds)
        self.cap.set(cv2.CAP_PROP_POS_FRAMES, frame_number)
        ret, frame = self.cap.read()

        if not ret or frame is None:
            raise ValueError(f"Cannot read frame at {time_in_seconds} seconds.")

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
            if category_id == 3:  # 3 corresponds to 'car'
                x_center, y_center, width, height = box.numpy()
                x1 = int((x_center - width / 2) * frame.shape[1])
                y1 = int((y_center - height / 2) * frame.shape[0])
                x2 = int((x_center + width / 2) * frame.shape[1])
                y2 = int((y_center + height / 2) * frame.shape[0])
                detected_boxes.append((x1, y1, x2, y2))

        return frame, detected_boxes

    def get_parking_status(self, detected_boxes):
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

        return parking_status
