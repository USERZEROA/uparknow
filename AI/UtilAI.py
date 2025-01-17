from AI import ParkingLotSurveillance

def StartParkingMonitor():
    video_path = 'Parkng_Lot_Surveillance_Video.mp4'
    json_path = 'flipped_car_coordinates_with_parking_positions.json'
    initial_time = 280

    surveillance = ParkingLotSurveillance(video_path)
    surveillance.set_parking_boxes(json_path)